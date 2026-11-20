package net.dzikoysk.funnyguilds.feature.purchase;

import dev.peri.yetanothermessageslibrary.replace.replacement.Replacement;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Stream;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.sections.GuildPurchaseConfiguration;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildEnlargeEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildExtendValidityEvent;
import net.dzikoysk.funnyguilds.feature.audit.GuildAuditEntry.Type;
import net.dzikoysk.funnyguilds.feature.command.GuildCommandPermission;
import net.dzikoysk.funnyguilds.feature.command.InternalValidationException;
import net.dzikoysk.funnyguilds.feature.hooks.vault.VaultHook;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class GuildPurchaseService {

    private final FunnyGuilds plugin;
    private final Set<UUID> purchasingGuilds = new HashSet<>();

    public GuildPurchaseService(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    public List<Duration> periods() {
        PluginConfiguration config = this.plugin.getPluginConfiguration();
        List<String> periods = config.validityPeriods.isEmpty() ? GuildPurchaseConfiguration.DEFAULT_RENEWAL_PERIODS : config.validityPeriods;
        return Stream.concat(Stream.of(config.validityTime), periods.stream().map(GuildPurchaseConfiguration::parsePeriod))
                .distinct().sorted().toList();
    }

    public RenewalQuote quoteRenewal(Guild guild, Duration period) {
        PluginConfiguration config = this.plugin.getPluginConfiguration();
        Instant now = Instant.now();
        Instant previous = guild.getValidity();
        Duration wait = Duration.between(now, previous).minus(config.validityWhen);
        try {
            Instant expiry = (Instant.EPOCH.equals(previous) ? now : previous).plus(period);
            var rules = config.validityPayment;
            PurchasePrice price = PurchasePrice.scale(rules.mode.includesItems() ? config.validityItems : List.of(),
                    rules.mode.includesMoney() ? rules.money : 0, period, rules.rateUnit(config.validityTime),
                    rules.mode.includesMoney() ? VaultHook.moneyScale() : 0);
            Block block = !this.periods().contains(period) ? Block.PERIOD
                    : !config.validityWhen.isZero() && wait.isPositive() ? Block.WAIT : Block.NONE;
            return new RenewalQuote(period, previous, expiry, wait, price, block);
        } catch (ArithmeticException | DateTimeException exception) {
            return new RenewalQuote(period, previous, previous, wait, PurchasePrice.FREE, Block.PRICE);
        }
    }

    public ExpansionQuote quoteExpansion(Guild guild, int level) {
        PluginConfiguration config = this.plugin.getPluginConfiguration();
        int size = this.plugin.getRegionManager().sizeAtEnlargementLevel(level);
        int maximum = config.getMaxEnlargementLevel();
        Region region = guild.getRegion().orElse(null);
        int current = region == null ? 0 : region.getEnlargementLevel();
        if (!config.regionsEnabled || region == null) {
            return new ExpansionQuote(region, current, level, size, maximum, PurchasePrice.FREE, Block.REGIONS);
        }
        if (level > maximum && level > current) {
            return new ExpansionQuote(region, current, level, size, maximum, PurchasePrice.FREE, Block.LIMIT);
        }
        PurchasePrice price = level <= maximum ? this.expansionPrice(config, level) : PurchasePrice.FREE;
        Block block;
        if (level < current) {
            block = Block.PURCHASED;
        } else if (level == current) {
            size = region.getSize();
            block = Block.CURRENT;
        } else if (level != current + 1) {
            block = Block.PREVIOUS;
        } else if (this.plugin.getRegionManager().isNearRegion(region.getCenter())) {
            block = Block.NEAR;
        } else {
            block = Block.NONE;
        }
        return new ExpansionQuote(region, current, level, size, maximum, price, block);
    }

    private PurchasePrice expansionPrice(PluginConfiguration config, int level) {
        if (level <= 0) {
            return PurchasePrice.FREE;
        }
        var rules = config.enlargePayment;
        return new PurchasePrice(
                rules.mode.includesItems() ? List.of(config.enlargeItems.get(level - 1).clone()) : List.of(),
                rules.mode.includesMoney() ? BigDecimal.valueOf(rules.money.get(level - 1))
                        .setScale(VaultHook.moneyScale(), RoundingMode.CEILING) : BigDecimal.ZERO);
    }

    public Funds funds(Player player, PurchasePrice price) {
        ItemStack[] storage = player.getInventory().getStorageContents();
        List<ItemStack> missing = new ArrayList<>();
        for (ItemStack item : price.items()) {
            long amount = 0;
            for (ItemStack slot : storage) {
                if (slot != null && item.isSimilar(slot)) {
                    amount += slot.getAmount();
                }
            }
            if (amount < item.getAmount()) {
                ItemStack deficit = item.clone();
                deficit.setAmount((int) (item.getAmount() - amount));
                missing.add(deficit);
            }
        }
        boolean hasMoneyCost = price.money().signum() > 0;
        boolean economyUnavailable = hasMoneyCost && !VaultHook.isEconomyHooked();
        boolean moneyDenied = hasMoneyCost && !economyUnavailable && !VaultHook.canAfford(player, price.money().doubleValue());
        BigDecimal moneyMissing = BigDecimal.ZERO;
        if (moneyDenied) {
            double balance = VaultHook.accountBalance(player);
            moneyMissing = Double.isFinite(balance) ? price.money().subtract(BigDecimal.valueOf(balance)).max(BigDecimal.ZERO) : price.money();
        }
        return new Funds(List.copyOf(missing), moneyMissing, economyUnavailable, moneyDenied);
    }

    public void renew(Player player, User actor, Guild guild, RenewalQuote displayed) {
        this.lock(guild);
        try {
            RenewalQuote before = this.quoteRenewal(guild, displayed.period());
            this.validateRenewal(before, displayed);
            this.checkFunds(player, before.price());
            if (!SimpleEventHandler.handle(new GuildExtendValidityEvent(EventCause.USER, actor, guild, before.period()))) {
                return;
            }
            this.requireAccess(player, actor, guild, GuildCommandPermission.EXTEND_VALIDITY, "funnyguilds.validity");
            RenewalQuote after = this.quoteRenewal(guild, before.period());
            this.validateRenewal(after, before);
            this.pay(player, guild, after.price(), () -> {
                this.requirePresence(player, actor, guild);
                RenewalQuote commit = this.quoteRenewal(guild, after.period());
                this.validateRenewal(commit, after);
                guild.setValidity(commit.expiry());
            });
            this.plugin.getGuildAuditService().record(guild, actor, Type.VALIDITY_RENEWED, Map.of(
                    "PERIOD", TimeUtils.formatTime(after.period()), "COST", after.price().describe()));
            var messages = this.plugin.getMessageService();
            messages.getMessage(config -> config.validityDone).receiver(player)
                    .with("{DATE}", messages.get(player, config -> config.dateFormat).format(guild.getValidity())).send();
        } finally {
            this.purchasingGuilds.remove(guild.getUUID());
        }
    }

    public void expand(Player player, User actor, Guild guild, ExpansionQuote displayed) {
        this.lock(guild);
        try {
            ExpansionQuote before = this.quoteExpansion(guild, displayed.level());
            this.validateExpansion(before, displayed);
            this.checkFunds(player, before.price());
            if (!SimpleEventHandler.handle(new GuildEnlargeEvent(EventCause.USER, actor, guild))) {
                return;
            }
            this.requireAccess(player, actor, guild, GuildCommandPermission.ENLARGE, "funnyguilds.enlarge");
            ExpansionQuote after = this.quoteExpansion(guild, before.level());
            this.validateExpansion(after, before);
            this.pay(player, guild, after.price(), () -> {
                this.requirePresence(player, actor, guild);
                ExpansionQuote commit = this.quoteExpansion(guild, after.level());
                this.validateExpansion(commit, after);
                this.plugin.getRegionManager().changeRegionEnlargement(commit.region(), commit.level());
            });
            this.plugin.getGuildAuditService().record(guild, actor, Type.TERRITORY_EXPANDED, Map.of(
                    "LEVEL", Integer.toString(after.level()), "SIZE", Integer.toString(after.size()), "COST", after.price().describe()));
            this.plugin.getMessageService().getMessage(config -> config.enlargeDone).receiver(guild)
                    .with(new FunnyFormatter().register("{SIZE}", after.size()).register("{LEVEL}", after.level())).send();
        } finally {
            this.purchasingGuilds.remove(guild.getUUID());
        }
    }

    private void lock(Guild guild) {
        when(!this.purchasingGuilds.add(guild.getUUID()), config -> config.purchaseBusy);
    }

    private void requireAccess(Player player, User actor, Guild guild, GuildCommandPermission permission, String bukkitPermission) {
        this.requirePresence(player, actor, guild);
        when(!player.hasPermission(bukkitPermission), config -> config.permission);
        when(!this.plugin.getGuildPermissionChecker().getPermissionValue(guild, actor, permission).orElse(false),
                config -> config.generalInsufficientGuildPermission);
    }

    private void requirePresence(Player player, User actor, Guild guild) {
        when(!player.isOnline() || actor.getGuild().filter(guild::equals).isEmpty()
                || this.plugin.getGuildManager().findByUuid(guild.getUUID()).filter(current -> current == guild).isEmpty(),
                config -> config.generalHasNoGuild);
    }

    private void validateRenewal(RenewalQuote current, RenewalQuote displayed) {
        when(!current.previous().equals(displayed.previous()) || !current.price().equals(displayed.price()), config -> config.purchaseChanged);
        switch (current.block()) {
            case WAIT -> throw new InternalValidationException(config -> config.validityWhen,
                    Replacement.string("{TIME}", TimeUtils.formatTime(current.waitTime())));
            case PERIOD -> throw new InternalValidationException(config -> config.validityPeriodUnavailable);
            case PRICE -> throw new InternalValidationException(config -> config.purchaseInvalidPrice);
            default -> { }
        }
    }

    private void validateExpansion(ExpansionQuote current, ExpansionQuote displayed) {
        when(current.region() != displayed.region() || current.current() != displayed.current()
                || current.size() != displayed.size() || !current.price().equals(displayed.price()), config -> config.purchaseChanged);
        switch (current.block()) {
            case REGIONS -> throw new InternalValidationException(config -> config.regionsDisabled);
            case LIMIT -> throw new InternalValidationException(config -> config.enlargeMaxSize);
            case NEAR -> throw new InternalValidationException(config -> config.enlargeIsNear);
            case PURCHASED, CURRENT, PREVIOUS -> throw new InternalValidationException(config -> config.purchaseChanged);
            default -> { }
        }
    }

    private void checkFunds(Player player, PurchasePrice price) {
        Funds funds = this.funds(player, price);
        when(funds.economyUnavailable(), config -> config.purchaseEconomyUnavailable);
        when(!funds.itemsMissing().isEmpty(), config -> config.purchaseMissingItems, Replacement.string("{ITEMS}", PurchasePrice.formatItems(funds.itemsMissing())));
        when(funds.moneyMissing().signum() > 0, config -> config.purchaseMissingMoney, Replacement.string("{MONEY}", funds.moneyMissing().toPlainString()));
        when(funds.moneyDenied(), config -> config.purchasePaymentFailed);
    }

    private void pay(Player player, Guild guild, PurchasePrice price, Runnable grant) {
        this.checkFunds(player, price);
        ItemStack[] charged = price.items().stream().map(ItemStack::clone).toArray(ItemStack[]::new);
        ItemStack[] requests = price.items().stream().map(ItemStack::clone).toArray(ItemStack[]::new);
        var remaining = player.getInventory().removeItem(requests);
        if (!remaining.isEmpty()) {
            remaining.forEach((index, item) -> charged[index].setAmount(charged[index].getAmount() - item.getAmount()));
            this.returnItems(player, charged);
            throw new InternalValidationException(config -> config.purchaseChanged);
        }

        boolean paid = false;
        try {
            if (price.money().signum() > 0) {
                EconomyResponse result;
                try {
                    result = VaultHook.withdrawFromPlayerBank(player, price.money().doubleValue());
                } catch (RuntimeException exception) {
                    this.recordUncertainPayment(player, guild, price, "withdraw", exception);
                    throw new InternalValidationException(config -> config.purchasePaymentFailed);
                }
                when(!result.transactionSuccess(), config -> config.purchasePaymentFailed);
                paid = true;
            }
            grant.run();
        } catch (RuntimeException exception) {
            this.returnItems(player, charged);
            if (paid) {
                try {
                    if (!VaultHook.refund(player, price.money().doubleValue()).transactionSuccess()) {
                        this.recordUncertainPayment(player, guild, price, "refund", null);
                    }
                } catch (RuntimeException refundFailure) {
                    this.recordUncertainPayment(player, guild, price, "refund", refundFailure);
                }
            }
            if (exception instanceof InternalValidationException validationException) {
                throw validationException;
            }
            this.plugin.getLogger().log(Level.SEVERE, "Could not apply a guild purchase for " + guild.getUUID(), exception);
            throw new InternalValidationException(config -> config.purchasePaymentFailed);
        }
    }

    private void returnItems(Player player, ItemStack[] charged) {
        ItemStack[] refund = Stream.of(charged).filter(item -> item.getAmount() > 0).toArray(ItemStack[]::new);
        player.getInventory().addItem(refund).values()
                .forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
    }

    private void recordUncertainPayment(Player player, Guild guild, PurchasePrice price, String step, Throwable cause) {
        String details = "Guild purchase " + step + " requires reconciliation. Player " + player.getUniqueId()
                + ", guild " + guild.getUUID() + ", money " + price.money().toPlainString();
        this.plugin.getLogger().log(Level.SEVERE, details, cause);
        Path report = this.plugin.getDataFolder().toPath().resolve("purchase-recovery.log");
        try {
            Files.writeString(report, Instant.now() + " " + details + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException exception) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not save Vault recovery entry to " + report, exception);
        }
    }

    public enum Block {
        NONE, WAIT, PERIOD, PRICE, REGIONS, LIMIT, PURCHASED, CURRENT, PREVIOUS, NEAR
    }

    public record Funds(List<ItemStack> itemsMissing, BigDecimal moneyMissing, boolean economyUnavailable, boolean moneyDenied) {

        public boolean affordable() {
            return !this.economyUnavailable && !this.moneyDenied && this.itemsMissing.isEmpty();
        }
    }

    public record RenewalQuote(Duration period, Instant previous, Instant expiry, Duration waitTime, PurchasePrice price, Block block) {
    }

    public record ExpansionQuote(Region region, int current, int level, int size, int maximum, PurchasePrice price, Block block) {
    }
}

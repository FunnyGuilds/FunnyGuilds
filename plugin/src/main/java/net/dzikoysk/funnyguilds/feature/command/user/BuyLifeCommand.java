package net.dzikoysk.funnyguilds.feature.command.user;

import dev.peri.yetanothermessageslibrary.replace.replacement.Replacement;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.config.sections.BuyLifeConfiguration;
import net.dzikoysk.funnyguilds.config.sections.BuyLifeConfiguration.BuyLifePaymentType;
import net.dzikoysk.funnyguilds.config.sections.WarConfiguration;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildBuyLifeEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildLifeGainEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildLifeGainEvent.GainReason;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildCommandPermission;
import net.dzikoysk.funnyguilds.feature.command.HasGuildPermission;
import net.dzikoysk.funnyguilds.feature.hooks.HookManager;
import net.dzikoysk.funnyguilds.feature.hooks.vault.VaultHook;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class BuyLifeCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.buyLife.name}",
            description = "${user.buyLife.description}",
            aliases = "${user.buyLife.aliases}",
            permission = "funnyguilds.buylife",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player, @HasGuildPermission(GuildCommandPermission.BUY_LIFE) User deputy, Guild guild) {
        BuyLifeConfiguration buyLifeConfig = this.config.buyLife;
        WarConfiguration warConfig = this.config.war;

        when(!buyLifeConfig.enabled, config -> config.buyLifeDisabled);

        int maxAllowed = buyLifeConfig.allowBuyUpToMaxLives ? warConfig.maxLives : warConfig.lives;
        when(guild.getLives() >= maxAllowed, config -> config.buyLifeMaxLives,
                Replacement.string("{LIVES}", guild.getLives()),
                Replacement.string("{MAX-LIVES}", maxAllowed));

        if (!buyLifeConfig.cooldown.isZero()) {
            guild.getLastLifeBuy().peek(lastBuy -> {
                Duration elapsed = Duration.between(lastBuy, Instant.now());
                if (elapsed.compareTo(buyLifeConfig.cooldown) < 0) {
                    when(true, config -> config.buyLifeCooldown,
                            Replacement.string("{TIME}", TimeUtils.formatTime(buyLifeConfig.cooldown.minus(elapsed))));
                }
            });
        }

        double multiplier = 1.0;
        if (buyLifeConfig.scalingCost.enabled && guild.getLives() > warConfig.lives) {
            multiplier += (guild.getLives() - warConfig.lives) * buyLifeConfig.scalingCost.costMultiplierPerLife;
        }

        double requiredMoney = buyLifeConfig.money * multiplier;
        List<ItemStack> requiredItems = new ArrayList<>();
        for (ItemStack item : buyLifeConfig.items) {
            ItemStack scaled = item.clone();
            scaled.setAmount((int) Math.ceil(item.getAmount() * multiplier));
            requiredItems.add(scaled);
        }

        boolean requireMoney = buyLifeConfig.paymentType == BuyLifePaymentType.MONEY || buyLifeConfig.paymentType == BuyLifePaymentType.BOTH;
        boolean requireItems = buyLifeConfig.paymentType == BuyLifePaymentType.ITEMS || buyLifeConfig.paymentType == BuyLifePaymentType.BOTH;

        if (requireMoney && requiredMoney > 0) {
            if (!HookManager.VAULT.isPresent() || !VaultHook.canAfford(player, requiredMoney)) {
                this.messageService.getMessage(config -> config.buyLifeNoMoney)
                        .receiver(player)
                        .with("{MONEY}", requiredMoney)
                        .send();
                return;
            }
        }

        if (requireItems && !requiredItems.isEmpty()) {
            if (!ItemUtils.playerHasEnoughItems(player, requiredItems, config -> config.buyLifeNoItems)) {
                return;
            }
        }

        GuildBuyLifeEvent buyEvent = new GuildBuyLifeEvent(EventCause.USER, deputy, guild, requiredMoney, requiredItems);
        if (!SimpleEventHandler.handle(buyEvent)) {
            return;
        }

        int livesBefore = guild.getLives();
        int livesAfter = livesBefore + 1;
        GuildLifeGainEvent gainEvent = new GuildLifeGainEvent(EventCause.USER, deputy, guild, GainReason.BUY, livesBefore, livesAfter);
        if (!SimpleEventHandler.handle(gainEvent)) {
            return;
        }

        if (requireItems && !requiredItems.isEmpty()) {
            player.getInventory().removeItem(ItemUtils.toArray(requiredItems));
        }

        if (requireMoney && requiredMoney > 0 && HookManager.VAULT.isPresent()) {
            VaultHook.withdrawFromPlayerBank(player, requiredMoney);
        }

        guild.updateLives(l -> l + 1);
        guild.setLastLifeBuy(Instant.now());

        this.messageService.getMessage(config -> config.buyLifeSuccess)
                .receiver(player)
                .with("{LIVES}", guild.getLives())
                .with("{MAX-LIVES}", warConfig.maxLives)
                .send();

        this.messageService.getMessage(config -> config.buyLifeBroadcast)
                .receiver(guild)
                .with("{PLAYER}", player.getName())
                .with("{LIVES}", guild.getLives())
                .with("{MAX-LIVES}", warConfig.maxLives)
                .send();
    }

}

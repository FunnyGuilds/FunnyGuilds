package net.dzikoysk.funnyguilds.feature.panel;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.audit.GuildAuditEntry;
import net.dzikoysk.funnyguilds.feature.base.GuildBaseService;
import net.dzikoysk.funnyguilds.feature.command.GuildCommandPermission;
import net.dzikoysk.funnyguilds.feature.command.InternalValidationException;
import net.dzikoysk.funnyguilds.feature.invitation.guild.GuildInvitation;
import net.dzikoysk.funnyguilds.feature.invitation.ally.AllyInvitation;
import net.dzikoysk.funnyguilds.feature.purchase.GuildPurchaseService.Funds;
import net.dzikoysk.funnyguilds.feature.purchase.PurchasePrice;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.permission.GuildPermission;
import net.dzikoysk.funnyguilds.guild.permission.GuildPermissionDecision;
import net.dzikoysk.funnyguilds.guild.permission.GuildRolePolicy;
import net.dzikoysk.funnyguilds.guild.permission.GuildRoleService;
import net.dzikoysk.funnyguilds.module.api.GuildPanelAccess;
import net.dzikoysk.funnyguilds.module.api.PanelAudit;
import net.dzikoysk.funnyguilds.rank.DefaultTops;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class PanelAccessAdapter implements GuildPanelAccess {

    private final FunnyGuilds plugin;
    private final Player player;
    private final User user;
    private final Guild guild;

    public PanelAccessAdapter(FunnyGuilds plugin, Player player, User user, Guild guild) {
        this.plugin = plugin;
        this.player = player;
        this.user = user;
        this.guild = guild;
    }

    @Override
    public boolean isActive() {
        return this.player.isOnline() && this.player.hasPermission("funnyguilds.panel")
                && this.user.getGuild().filter(this.guild::equals).isPresent()
                && this.plugin.getGuildManager().findByUuid(this.guild.getUUID()).filter(found -> found == this.guild).isPresent();
    }

    @Override public String tag() { return this.guild.getTag(); }
    @Override public boolean pvp() { return this.guild.hasPvPEnabled(); }
    @Override public boolean allyPvpAllowed() { return this.plugin.getPluginConfiguration().damageAlly; }
    @Override public Instant validity() { return this.guild.getValidity(); }
    @Override public String duration(Duration duration) { return TimeUtils.formatTime(duration); }

    @Override
    public String date(Instant date) {
        return this.plugin.getMessageService().get(this.player, config -> config.dateFormat).format(date);
    }

    @Override
    public boolean teleport() {
        return this.runAction("funnyguilds.base", GuildCommandPermission.BASE,
                () -> this.plugin.getGuildBaseService().teleport(this.player, this.user, this.guild));
    }

    @Override
    public List<Relation> relations() {
        var invitations = this.plugin.getAllyInvitationList();
        Set<UUID> incoming = invitations.getInvitationsFor(this.guild).stream()
                .map(AllyInvitation::getFromUUID).collect(Collectors.toUnmodifiableSet());
        Set<UUID> outgoing = invitations.getInvitationsFrom(this.guild).stream()
                .map(AllyInvitation::getToUUID).collect(Collectors.toUnmodifiableSet());
        return this.plugin.getGuildManager().getGuilds().stream().filter(target -> !target.equals(this.guild))
                .map(target -> this.relation(target, incoming.contains(target.getUUID()), outgoing.contains(target.getUUID())))
                .toList();
    }

    @Override
    public List<Relation> allies() {
        return this.guild.getAllies().stream().map(ally -> this.relation(ally, false, false)).toList();
    }

    @Override
    public Optional<Relation> relation(UUID id) {
        var invitations = this.plugin.getAllyInvitationList();
        return this.plugin.getGuildManager().findByUuid(id)
                .filter(target -> !target.equals(this.guild))
                .map(target -> this.relation(target, invitations.hasInvitation(target, this.guild), invitations.hasInvitation(this.guild, target)));
    }

    private Relation relation(Guild target, boolean incoming, boolean outgoing) {
        return new Relation(target.getUUID(), target.getName(), target.getTag(), this.guild.isAlly(target),
                incoming, outgoing, target.isEnemy(this.guild), this.guild.isEnemy(target),
                this.guild.hasAllyPvPEnabled(target), target.hasAllyPvPEnabled(this.guild));
    }

    @Override
    public boolean togglePvp() {
        return this.runAction("funnyguilds.pvp", GuildCommandPermission.PVP,
                () -> this.plugin.getGuildDiplomacyService().togglePvp(this.user, this.guild));
    }

    @Override
    public boolean changeRelation(RelationAction action, UUID id) {
        GuildCommandPermission permission = switch (action) {
            case ALLY_PVP -> GuildCommandPermission.PVP;
            case DECLARE, WITHDRAW -> GuildCommandPermission.WAR;
            default -> GuildCommandPermission.ALLY;
        };
        String node = switch (action) {
            case ALLY_PVP -> "funnyguilds.pvp";
            case DECLARE, WITHDRAW -> "funnyguilds.war";
            case BREAK -> "funnyguilds.break";
            default -> "funnyguilds.ally";
        };
        return this.runAction(node, permission, () -> {
            Guild target = this.plugin.getGuildManager().findByUuid(id)
                    .orElseThrow(() -> new InternalValidationException(config -> config.allyInvitationChanged));
            var diplomacy = this.plugin.getGuildDiplomacyService();
            switch (action) {
                case ALLY_PVP -> diplomacy.toggleAllyPvp(this.user, this.guild, target);
                case BREAK -> diplomacy.breakAlliance(this.user, this.guild, target);
                case ACCEPT -> diplomacy.accept(this.user, this.guild, target);
                case REVOKE -> diplomacy.revoke(this.user, this.guild, target);
                case INVITE -> diplomacy.invite(this.user, this.guild, target);
                case DECLARE -> diplomacy.declareWar(this.user, this.guild, target);
                case WITHDRAW -> diplomacy.withdrawWar(this.user, this.guild, target);
            }
        });
    }

    @Override
    public List<Duration> periods() {
        return this.plugin.getGuildPurchaseService().periods();
    }

    @Override
    public Renewal renewal(Duration period) {
        var service = this.plugin.getGuildPurchaseService();
        var quote = service.quoteRenewal(this.guild, period);
        return new Renewal(quote.expiry(), quote.waitTime(), Block.valueOf(quote.block().name()),
                this.payment(quote.price(), () -> this.runAction("funnyguilds.validity", GuildCommandPermission.EXTEND_VALIDITY,
                        () -> service.renew(this.player, this.user, this.guild, quote))));
    }

    @Override
    public Expansion expansion(int level) {
        var service = this.plugin.getGuildPurchaseService();
        var quote = service.quoteExpansion(this.guild, level);
        return new Expansion(quote.current(), quote.size(), quote.maximum(), Block.valueOf(quote.block().name()),
                this.payment(quote.price(), () -> this.runAction("funnyguilds.enlarge", GuildCommandPermission.ENLARGE,
                        () -> service.expand(this.player, this.user, this.guild, quote))));
    }

    private Payment payment(PurchasePrice price, BooleanSupplier confirm) {
        Funds funds = this.plugin.getGuildPurchaseService().funds(this.player, price);
        return new Payment(formatCost(price.items(), price.money()), formatCost(funds.itemsMissing(), funds.moneyMissing()),
                funds.affordable(), funds.economyUnavailable(), confirm);
    }

    private static String formatCost(List<ItemStack> items, BigDecimal money) {
        String material = PurchasePrice.formatItems(items);
        if (money.signum() == 0) {
            return material;
        }
        return material.isEmpty() ? money.toPlainString() : material + " + " + money.toPlainString();
    }

    @Override public boolean owner() { return this.guild.isOwner(this.user); }
    @Override public boolean canManageRoles() { return this.plugin.getGuildRoleService().canManage(this.guild, this.user); }
    @Override public GuildRolePolicy roles() { return this.guild.getRolePolicy(); }

    @Override
    public List<Member> members() {
        return this.guild.getMembers().stream().map(this::member).toList();
    }

    @Override
    public List<Member> inviteTargets(boolean pending) {
        var invitations = this.plugin.getGuildInvitationList();
        var targets = pending ? invitations.getInvitationsFrom(this.guild).stream().map(GuildInvitation::getTo)
                : this.plugin.getServer().getOnlinePlayers().stream()
                        .flatMap(candidate -> this.plugin.getUserManager().findByPlayer(candidate).stream())
                        .filter(target -> !target.hasGuild() && !target.isVanished() && !invitations.hasInvitation(this.guild, target));
        return targets.map(this::member).toList();
    }

    private Member member(User target) {
        return new Member(target.getUUID(), target.getName(), this.guild.isOwner(target), this.guild.isDeputy(target));
    }

    @Override public boolean canInvite() { return this.hasPermission("funnyguilds.invite", GuildCommandPermission.INVITE); }
    @Override public boolean canKick() { return this.hasPermission("funnyguilds.kick", GuildCommandPermission.KICK); }

    private boolean hasPermission(String node, GuildCommandPermission permission) {
        return this.player.hasPermission(node)
                && this.plugin.getGuildPermissionChecker().getPermissionValue(this.guild, this.user, permission).orElse(false);
    }

    @Override
    public boolean changeInvitation(UUID id, boolean revoke) {
        return this.runAction("funnyguilds.invite", GuildCommandPermission.INVITE, () -> {
            User target = this.target(id);
            var service = this.plugin.getGuildMembershipService();
            if (revoke) {
                service.revoke(this.user, this.guild, target);
            } else {
                service.invite(this.user, this.guild, target);
            }
        });
    }

    @Override
    public boolean kick(UUID id) {
        return this.runAction("funnyguilds.kick", GuildCommandPermission.KICK,
                () -> this.plugin.getGuildMembershipService().kick(this.user, this.guild, this.target(id)));
    }

    @Override
    public void nameInputTimedOut() {
        this.plugin.getMessageService().getMessage(config -> config.guildRoleInputTimedOut).receiver(this.player).send();
    }

    @Override
    public List<Permission> permissions() {
        return this.plugin.getGuildRoleService().permissions().stream()
                .map(permission -> new Permission(permission.key().value(), permission.key().asString(),
                        permission != GuildCommandPermission.MANAGE_PERMISSIONS)).toList();
    }

    @Override
    public UUID selectedRole(UUID id) {
        return this.plugin.getUserManager().findByUuid(id).map(target -> this.plugin.getGuildRoleService().selectedRole(this.guild, target))
                .orElse(GuildRolePolicy.MEMBER);
    }

    @Override
    public GuildPermissionDecision roleDecision(UUID role, Permission permission) {
        return GuildRoleService.roleDecision(this.roles(), role, this.permission(permission));
    }

    @Override
    public GuildPermissionDecision memberDecision(UUID id, Permission permission) {
        return this.plugin.getUserManager().findByUuid(id)
                .map(target -> this.plugin.getGuildPermissionChecker().getStoredDecision(this.guild, target, this.permission(permission)))
                .orElseGet(() -> new GuildPermissionDecision(false, GuildPermissionDecision.Source.DEFAULT));
    }

    @Override
    public boolean createRole(UUID role, String name) {
        return this.manage(() -> this.plugin.getGuildRoleService().createRole(this.guild, this.user, role, name));
    }

    @Override
    public boolean deleteRole(UUID role) {
        return this.manage(() -> this.plugin.getGuildRoleService().deleteRole(this.guild, this.user, role));
    }

    @Override
    public boolean assignRole(UUID id, UUID role) {
        return this.manage(() -> this.plugin.getGuildRoleService().assign(this.guild, this.user, this.target(id), role));
    }

    @Override
    public boolean setRolePermission(UUID role, Permission permission, Boolean value) {
        return this.manage(() -> this.plugin.getGuildRoleService().setRolePermission(this.guild, this.user, role, this.permission(permission), value));
    }

    @Override
    public boolean setOverride(UUID id, Permission permission, Boolean value) {
        return this.manage(() -> this.plugin.getGuildRoleService().setOverride(this.guild, this.user, this.target(id), this.permission(permission), value));
    }

    @Override
    public boolean setDelegation(Permission permission, boolean delegated) {
        return this.manage(() -> this.plugin.getGuildRoleService().setDelegation(this.guild, this.user, this.permission(permission), delegated));
    }

    private boolean manage(Runnable action) {
        return this.runAction("funnyguilds.panel", GuildCommandPermission.MANAGE_PERMISSIONS, action);
    }

    private GuildPermission<Boolean> permission(Permission permission) {
        return this.plugin.getGuildRoleService().permissions().stream()
                .filter(candidate -> candidate.key().asString().equals(permission.policyKey())).findFirst()
                .orElseThrow(() -> new InternalValidationException(config -> config.guildRoleDenied));
    }

    private User target(UUID id) {
        return this.plugin.getUserManager().findByUuid(id)
                .orElseThrow(() -> new InternalValidationException(config -> config.memberInvitationChanged));
    }

    public Map<String, String> overview() {
        Duration remaining = Duration.between(Instant.now(), this.guild.getValidity());
        String deputies = this.guild.getDeputies().stream().map(User::getName).sorted().collect(Collectors.joining(", "));
        GuildBaseService base = this.plugin.getGuildBaseService();
        String baseCost = base.requiredItems(this.player).stream()
                .map(item -> item.getAmount() + " × " + item.getType().name().toLowerCase(Locale.ROOT))
                .collect(Collectors.joining(", "));
        return Map.ofEntries(
                Map.entry("GUILD", this.guild.getName()),
                Map.entry("TAG", this.guild.getTag()),
                Map.entry("OWNER", this.guild.getOwner().getName()),
                Map.entry("DEPUTIES", deputies),
                Map.entry("ONLINE", String.valueOf(this.guild.getOnlineMembers(false).size())),
                Map.entry("MEMBERS", String.valueOf(this.guild.getMembers().size())),
                Map.entry("RANK", String.valueOf(this.guild.getRank().getPosition(DefaultTops.GUILD_AVG_POINTS_TOP))),
                Map.entry("POINTS", String.valueOf(this.guild.getRank().getPoints())),
                Map.entry("LIVES", String.valueOf(this.guild.getLives())),
                Map.entry("EXPIRY", this.plugin.getMessageService().get(this.player, config -> config.dateFormat).format(this.guild.getValidity())),
                Map.entry("REMAINING", TimeUtils.formatTime(remaining.isNegative() ? Duration.ZERO : remaining)),
                Map.entry("LEVEL", this.guild.getRegion().map(Region::getEnlargementLevel).map(String::valueOf).orElse("")),
                Map.entry("SIZE", this.guild.getRegion().map(Region::getSize).map(String::valueOf).orElse("")),
                Map.entry("ALLIES", String.valueOf(this.guild.getAllies().size())),
                Map.entry("BASE_COST", baseCost),
                Map.entry("BASE_DELAY", base.delay(this.player).toSeconds() + " s")
        );
    }

    public PanelAudit audit() {
        return new PanelAudit() {
            @Override
            public String guildTag() {
                return PanelAccessAdapter.this.guild.getTag();
            }

            @Override
            public List<String> categories() {
                return Arrays.stream(GuildAuditEntry.Category.values()).map(category -> auditKey(category.name())).toList();
            }

            @Override
            public Optional<List<Entry>> read() {
                if (!PanelAccessAdapter.this.runAction("funnyguilds.panel", GuildCommandPermission.VIEW_AUDIT, () -> { })) {
                    return Optional.empty();
                }
                var dateFormat = PanelAccessAdapter.this.plugin.getMessageService().get(PanelAccessAdapter.this.player, config -> config.dateFormat);
                return Optional.of(PanelAccessAdapter.this.plugin.getGuildAuditService().entries(PanelAccessAdapter.this.guild).stream()
                        .map(entry -> new Entry(dateFormat.format(Instant.ofEpochMilli(entry.time())), entry.actorName(),
                                auditKey(entry.type().category().name()), auditKey(entry.type().name()), entry.details()))
                        .toList());
            }
        };
    }

    private static String auditKey(String name) {
        return name.toLowerCase(Locale.ROOT).replace('_', '-');
    }

    private boolean runAction(String permission, GuildCommandPermission guildPermission, Runnable action) {
        if (!this.isActive()) {
            return false;
        }
        try {
            if (!this.player.hasPermission("funnyguilds.panel") || !this.player.hasPermission(permission)) {
                throw new InternalValidationException(config -> config.permission);
            }
            if (this.plugin.getGuildPermissionChecker().handlePermission(this.guild, this.user, guildPermission)) {
                if (guildPermission == GuildCommandPermission.BASE) {
                    this.player.closeInventory();
                }
                action.run();
                return true;
            }
        } catch (InternalValidationException exception) {
            this.plugin.getMessageService().getMessage(exception.getMessageSupplier())
                    .receiver(this.player)
                    .with(exception.getReplacements())
                    .send();
        }
        return false;
    }

}

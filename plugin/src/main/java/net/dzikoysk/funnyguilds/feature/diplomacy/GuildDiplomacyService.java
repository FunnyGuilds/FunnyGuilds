package net.dzikoysk.funnyguilds.feature.diplomacy;

import dev.peri.yetanothermessageslibrary.message.Sendable;
import dev.peri.yetanothermessageslibrary.replace.replacement.Replacement;
import java.util.Map;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.message.MessageConfiguration;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.ally.GuildAcceptAllyInvitationEvent;
import net.dzikoysk.funnyguilds.event.guild.ally.GuildBreakAllyEvent;
import net.dzikoysk.funnyguilds.event.guild.ally.GuildRevokeAllyInvitationEvent;
import net.dzikoysk.funnyguilds.event.guild.ally.GuildSendAllyInvitationEvent;
import net.dzikoysk.funnyguilds.feature.audit.GuildAuditEntry.Type;
import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardGlobalUpdateUserSyncTask;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class GuildDiplomacyService {

    private final FunnyGuilds plugin;

    public GuildDiplomacyService(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    public void togglePvp(User actor, Guild guild) {
        boolean enabled = guild.togglePvP();
        this.plugin.getGuildAuditService().record(guild, actor, Type.PVP_CHANGED, Map.of("STATE", String.valueOf(enabled)));
        this.plugin.getMessageService().getMessage(config -> enabled ? config.pvpOn : config.pvpOff)
                .receiver(actor).send();
    }

    public void toggleAllyPvp(User actor, Guild guild, Guild ally) {
        when(!this.plugin.getPluginConfiguration().damageAlly, config -> config.generalAllyPvpDisabled);
        this.requireTarget(ally);
        Replacement tag = Replacement.string("{TAG}", ally.getTag());
        when(!guild.isAlly(ally), config -> config.allyDoesntExist, tag);
        boolean enabled = guild.toggleAllyPvP(ally);
        this.plugin.getGuildAuditService().recordRelation(guild, ally, actor, Type.ALLY_PVP_CHANGED, Map.of("STATE", String.valueOf(enabled)));
        this.plugin.getMessageService().getMessage(config -> enabled ? config.pvpAllyOn : config.pvpAllyOff)
                .receiver(actor).with(tag).send();
    }

    public void handleAllyCommand(User actor, Guild guild, Guild target) {
        var invitations = this.plugin.getAllyInvitationList();
        if (invitations.hasInvitation(target, guild)) {
            this.accept(actor, guild, target);
        } else if (invitations.hasInvitation(guild, target)) {
            this.revoke(actor, guild, target);
        } else {
            this.invite(actor, guild, target);
        }
    }

    public void invite(User actor, Guild guild, Guild target) {
        this.validateAlliance(guild, target);
        var invitations = this.plugin.getAllyInvitationList();
        when(invitations.hasInvitation(guild, target) || invitations.hasInvitation(target, guild), config -> config.allyInvitationChanged);
        if (SimpleEventHandler.handle(new GuildSendAllyInvitationEvent(EventCause.USER, actor, guild, target))) {
            if (guild.isEnemy(target)) {
                this.withdrawWar(actor, guild, target);
            }
            invitations.createInvitation(guild, target);
            this.plugin.getGuildAuditService().recordRelation(guild, target, actor, Type.ALLIANCE_INVITED);
            this.notifyParties(actor, guild, target, config -> config.allyInviteDone, config -> config.allyToInvited);
        }
    }

    public void accept(User actor, Guild guild, Guild target) {
        this.validateAlliance(guild, target);
        var invitations = this.plugin.getAllyInvitationList();
        when(!invitations.hasInvitation(target, guild), config -> config.allyInvitationChanged);
        if (SimpleEventHandler.handle(new GuildAcceptAllyInvitationEvent(EventCause.USER, actor, guild, target))) {
            this.withdrawForAlliance(actor, guild, target);
            invitations.expireInvitation(target, guild);
            invitations.expireInvitation(guild, target);
            guild.addAlly(target);
            target.addAlly(guild);
            this.plugin.getGuildAuditService().recordRelation(guild, target, actor, Type.ALLIANCE_FORMED);
            this.notifyParties(actor, guild, target, config -> config.allyDone, config -> config.allyIDone);
            this.refreshNameTags(guild, target);
        }
    }

    public void revoke(User actor, Guild guild, Guild target) {
        this.requireTarget(target);
        var invitations = this.plugin.getAllyInvitationList();
        when(!invitations.hasInvitation(guild, target), config -> config.allyInvitationChanged);
        if (SimpleEventHandler.handle(new GuildRevokeAllyInvitationEvent(EventCause.USER, actor, guild, target))) {
            invitations.expireInvitation(guild, target);
            this.plugin.getGuildAuditService().recordRelation(guild, target, actor, Type.ALLIANCE_INVITATION_REVOKED);
            this.notifyParties(actor, guild, target, config -> config.allyReturn, config -> config.allyIReturn);
        }
    }

    public void breakAlliance(User actor, Guild guild, Guild target) {
        this.requireTarget(target);
        when(!guild.isAlly(target), config -> config.breakAllyExists);
        if (SimpleEventHandler.handle(new GuildBreakAllyEvent(EventCause.USER, actor, guild, target))) {
            guild.removeAlly(target);
            target.removeAlly(guild);
            this.plugin.getGuildAuditService().recordRelation(guild, target, actor, Type.ALLIANCE_BROKEN);
            this.refreshNameTags(guild, target);
            this.notifyParties(actor, guild, target, config -> config.breakDone, config -> config.breakIDone);
        }
    }

    public void declareWar(User actor, Guild guild, Guild target) {
        this.requireTarget(target);
        int limit = this.plugin.getPluginConfiguration().maxEnemiesBetweenGuilds;
        FunnyFormatter formatter = relationFormatter(target).register("{AMOUNT}", limit);
        when(guild.equals(target), config -> config.enemySame);
        when(guild.isAlly(target), config -> config.enemyAlly);
        when(guild.isEnemy(target), config -> config.enemyAlready);
        when(guild.getEnemies().size() >= limit, config -> config.enemyMaxAmount, formatter);
        when(target.getEnemies().size() >= limit, config -> config.enemyMaxTargetAmount, formatter);
        guild.addEnemy(target);
        this.plugin.getGuildAuditService().recordRelation(guild, target, actor, Type.WAR_DECLARED);
        this.notifyParties(actor, guild, target, config -> config.enemyDone, config -> config.enemyIDone);
        this.refreshNameTags(guild, target);
    }

    public void withdrawWar(User actor, Guild guild, Guild target) {
        this.requireTarget(target);
        when(!guild.isEnemy(target), config -> config.enemyNoDeclaration);
        this.withdrawIfDeclared(actor, guild, target);
        this.refreshNameTags(guild, target);
    }

    private void withdrawForAlliance(User actor, Guild guild, Guild target) {
        boolean guildDeclared = guild.isEnemy(target);
        boolean targetDeclared = target.isEnemy(guild);
        if (!guildDeclared && !targetDeclared) {
            return;
        }

        guild.removeEnemy(target);
        target.removeEnemy(guild);
        this.plugin.getGuildAuditService().recordRelation(guild, target, actor, Type.WAR_WITHDRAWN);
        if (guildDeclared) {
            this.notifyParties(actor, guild, target, config -> config.enemyEnd, config -> config.enemyIEnd);
        }
        if (targetDeclared) {
            var messages = this.plugin.getMessageService();
            messages.getMessage(config -> config.enemyEnd).receiver(target.getOwner()).with(relationFormatter(guild)).send();
            messages.getMessage(config -> config.enemyIEnd).receiver(guild.getOwner()).with(relationFormatter(target)).send();
        }
    }

    private void withdrawIfDeclared(User actor, Guild guild, Guild target) {
        if (guild.isEnemy(target)) {
            guild.removeEnemy(target);
            this.plugin.getGuildAuditService().recordRelation(guild, target, actor, Type.WAR_WITHDRAWN);
            this.notifyParties(actor, guild, target, config -> config.enemyEnd, config -> config.enemyIEnd);
        }
    }

    private void validateAlliance(Guild guild, Guild target) {
        this.requireTarget(target);
        int limit = this.plugin.getPluginConfiguration().maxAlliesBetweenGuilds;
        when(guild.equals(target), config -> config.allySame);
        when(guild.isAlly(target), config -> config.allyAlly);
        when(guild.getAllies().size() >= limit, config -> config.inviteAllyAmount, Replacement.string("{AMOUNT}", limit));
        when(target.getAllies().size() >= limit, config -> config.inviteAllyTargetAmount, relationFormatter(target).register("{AMOUNT}", limit));
    }

    private void requireTarget(Guild target) {
        when(this.plugin.getGuildManager().findByUuid(target.getUUID()).isEmpty(),
                config -> config.generalGuildNotExists, Replacement.string("{TAG}", target.getTag()));
    }

    private void refreshNameTags(Guild guild, Guild target) {
        this.plugin.getIndividualNameTagManager().ifPresent(manager -> {
            guild.getMembers().forEach(member -> this.plugin.scheduleFunnyTasks(new ScoreboardGlobalUpdateUserSyncTask(manager, member)));
            target.getMembers().forEach(member -> this.plugin.scheduleFunnyTasks(new ScoreboardGlobalUpdateUserSyncTask(manager, member)));
        });
    }

    private void notifyParties(User actor, Guild guild, Guild target,
                               Function<MessageConfiguration, Sendable> ownMessage,
                               Function<MessageConfiguration, Sendable> targetMessage) {
        var messages = this.plugin.getMessageService();
        messages.getMessage(ownMessage).receiver(actor).with(relationFormatter(target)).send();
        messages.getMessage(targetMessage).receiver(target.getOwner()).with(relationFormatter(guild)).send();
    }

    private static FunnyFormatter relationFormatter(Guild guild) {
        return new FunnyFormatter().register("{GUILD}", guild.getName()).register("{TAG}", guild.getTag());
    }
}

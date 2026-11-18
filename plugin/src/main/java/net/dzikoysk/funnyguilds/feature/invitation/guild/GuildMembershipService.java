package net.dzikoysk.funnyguilds.feature.invitation.guild;

import java.util.Map;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberInviteEvent;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberKickEvent;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberRevokeInviteEvent;
import net.dzikoysk.funnyguilds.feature.audit.GuildAuditEntry.Type;
import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardGlobalUpdateUserSyncTask;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class GuildMembershipService {

    private final FunnyGuilds plugin;

    public GuildMembershipService(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    public void toggleInvitation(User actor, Guild guild, User target) {
        if (this.plugin.getGuildInvitationList().hasInvitation(guild, target)) {
            this.revoke(actor, guild, target);
        } else {
            this.invite(actor, guild, target);
        }
    }

    public void invite(User actor, Guild guild, User target) {
        this.requireTarget(target);
        FunnyFormatter formatter = this.invitationFormatter(actor, guild);
        when(guild.getMembers().size() >= this.plugin.getPluginConfiguration().maxMembersInGuild, config -> config.inviteAmount, formatter);
        when(target.hasGuild(), config -> config.generalUserHasGuild);
        when(this.plugin.getGuildInvitationList().hasInvitation(guild, target), config -> config.memberInvitationChanged);
        if (!SimpleEventHandler.handle(new GuildMemberInviteEvent(EventCause.USER, actor, guild, target))) {
            return;
        }

        this.plugin.getGuildInvitationList().createInvitation(guild, target);
        this.plugin.getGuildAuditService().record(guild, actor, Type.INVITED, Map.of("TARGET", target.getName()));
        var messages = this.plugin.getMessageService();
        messages.getMessage(config -> config.inviteToOwner).receiver(actor).with("{PLAYER}", target.getName()).send();
        messages.getMessage(config -> config.inviteToInvited).receiver(target).with(formatter).send();
    }

    public void revoke(User actor, Guild guild, User target) {
        this.requireTarget(target);
        var invitations = this.plugin.getGuildInvitationList();
        when(!invitations.hasInvitation(guild, target), config -> config.memberInvitationChanged);
        if (!SimpleEventHandler.handle(new GuildMemberRevokeInviteEvent(EventCause.USER, actor, guild, target))) {
            return;
        }

        invitations.expireInvitation(guild, target);
        this.plugin.getGuildAuditService().record(guild, actor, Type.INVITATION_REVOKED, Map.of("TARGET", target.getName()));
        var messages = this.plugin.getMessageService();
        messages.getMessage(config -> config.inviteCancelled).receiver(actor).with("{PLAYER}", target.getName()).send();
        messages.getMessage(config -> config.inviteCancelledToInvited).receiver(target).with(this.invitationFormatter(actor, guild)).send();
    }

    public void kick(User deputy, Guild guild, User formerUser) {
        this.requireTarget(formerUser);
        when(!formerUser.hasGuild(), config -> config.generalPlayerHasNoGuild);
        when(formerUser.getGuild().filter(guild::equals).isEmpty() || !guild.isMember(formerUser), config -> config.kickOtherGuild);
        when(formerUser.isOwner(), config -> config.kickOwner);

        if (!SimpleEventHandler.handle(new GuildMemberKickEvent(EventCause.USER, deputy, guild, formerUser))) {
            return;
        }

        guild.removeMember(formerUser);
        formerUser.removeGuild();
        this.plugin.getGuildAuditService().record(guild, deputy, Type.KICKED, Map.of("TARGET", formerUser.getName()));
        this.plugin.getIndividualNameTagManager()
                .map(manager -> new ScoreboardGlobalUpdateUserSyncTask(manager, formerUser))
                .ifPresent(this.plugin::scheduleFunnyTasks);

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{PLAYER}", formerUser.getName())
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag());

        this.plugin.getMessageService().getMessage(config -> config.kickToOwner)
                .receiver(deputy)
                .with(formatter)
                .send();
        this.plugin.getMessageService().getMessage(config -> config.kickToPlayer)
                .receiver(formerUser)
                .with(formatter)
                .send();
        this.plugin.getMessageService().getMessage(config -> config.broadcastKick)
                .all()
                .with(formatter)
                .send();
    }

    private void requireTarget(User target) {
        when(this.plugin.getUserManager().findByUuid(target.getUUID()).filter(user -> user == target).isEmpty(),
                config -> config.memberInvitationChanged);
    }

    private FunnyFormatter invitationFormatter(User actor, Guild guild) {
        return new FunnyFormatter()
                .register("{AMOUNT}", this.plugin.getPluginConfiguration().maxMembersInGuild)
                .register("{OWNER}", actor.getName())
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag());
    }
}

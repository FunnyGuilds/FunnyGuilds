package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberInviteEvent;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberRevokeInviteEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.CanManage;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.feature.invitation.guild.GuildInvitationList;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.inject.annotations.Inject;
import panda.std.Option;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class InviteCommand extends AbstractFunnyCommand {

    @Inject
    public GuildInvitationList guildInvitationList;

    @FunnyCommand(
            name = "${user.invite.name}",
            description = "${user.invite.description}",
            aliases = "${user.invite.aliases}",
            permission = "funnyguilds.invite",
            completer = "online-players:3",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player, @CanManage User user, Guild guild, String[] args) {
        FunnyFormatter formatter = new FunnyFormatter()
                .register("{AMOUNT}", config.maxMembersInGuild)
                .register("{OWNER}", player.getName())
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag());

        when(args.length < 1, messages.generalNoNickGiven);
        when(guild.getMembers().size() >= config.maxMembersInGuild, formatter.format(messages.inviteAmount));

        User invitedUser = UserValidation.requireUserByName(args[0]);
        Option<Player> invitedPlayerOption = funnyServer.getPlayer(invitedUser.getUUID());

        if (guildInvitationList.hasInvitation(guild, invitedUser)) {
            if (!SimpleEventHandler.handle(new GuildMemberRevokeInviteEvent(EventCause.USER, user, guild, invitedUser))) {
                return;
            }

            guildInvitationList.expireInvitation(guild, invitedUser);
            user.sendMessage(messages.inviteCancelled);

            when(invitedPlayerOption.isPresent(), formatter.format(messages.inviteCancelledToInvited));
            return;
        }

        when(invitedPlayerOption.isEmpty(), messages.invitePlayerExists);
        when(invitedUser.hasGuild(), messages.generalUserHasGuild);

        if (!SimpleEventHandler.handle(new GuildMemberInviteEvent(EventCause.USER, user, guild, invitedUser))) {
            return;
        }

        guildInvitationList.createInvitation(guild, invitedUser);

        Player invitedPlayer = invitedPlayerOption.get();
        user.sendMessage(FunnyFormatter.formatOnce(messages.inviteToOwner, "{PLAYER}", invitedPlayer.getName()));
        sendMessage(invitedPlayer, formatter.format(messages.inviteToInvited));
    }

}

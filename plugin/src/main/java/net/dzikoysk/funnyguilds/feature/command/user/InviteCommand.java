package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.data.util.InvitationList;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberInviteEvent;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberRevokeInviteEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.CanManage;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import panda.std.Option;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class InviteCommand extends AbstractFunnyCommand {

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
        when(args.length < 1, messages.generalNoNickGiven);
        when(guild.getMembers().size() >= config.maxMembersInGuild, messages.inviteAmount.replace("{AMOUNT}", Integer.toString(config.maxMembersInGuild)));

        User invitedUser = UserValidation.requireUserByName(args[0]);
        Option<Player> invitedPlayerOption = invitedUser.getPlayer();

        if (InvitationList.hasInvitationFrom(invitedUser, guild)) {
            if (!SimpleEventHandler.handle(new GuildMemberRevokeInviteEvent(EventCause.USER, user, guild, invitedUser))) {
                return;
            }

            InvitationList.expireInvitation(guild, invitedUser);
            user.sendMessage(messages.inviteCancelled);
            when(invitedPlayerOption.isPresent(), messages.inviteCancelledToInvited.replace("{OWNER}", player.getName()).replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));
            return;
        }

        when(invitedPlayerOption.isEmpty(), messages.invitePlayerExists);
        Player invitedPlayer = invitedPlayerOption.get();
        when(invitedUser.hasGuild(), messages.generalUserHasGuild);

        if (!SimpleEventHandler.handle(new GuildMemberInviteEvent(EventCause.USER, user, guild, invitedUser))) {
            return;
        }

        InvitationList.createInvitation(guild, invitedPlayer);
        user.sendMessage(messages.inviteToOwner.replace("{PLAYER}", invitedPlayer.getName()));
        sendMessage(invitedPlayer, messages.inviteToInvited.replace("{OWNER}", player.getName()).replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));
    }

}

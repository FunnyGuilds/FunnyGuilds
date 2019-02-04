package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.util.InvitationList;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberInviteEvent;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberRevokeInviteEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcInvite implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
        Player player = (Player) sender;
        User user = User.get(player);

        if (!user.hasGuild()) {
            player.sendMessage(messages.generalHasNoGuild);
            return;
        }

        if (!user.isOwner() && !user.isDeputy()) {
            player.sendMessage(messages.generalIsNotOwner);
            return;
        }

        if (args.length < 1) {
            player.sendMessage(messages.generalNoNickGiven);
            return;
        }

        Guild guild = user.getGuild();

        if (guild.getMembers().size() >= FunnyGuilds.getInstance().getPluginConfiguration().inviteMembers) {
            player.sendMessage(messages.inviteAmount.replace("{AMOUNT}", Integer.toString(FunnyGuilds.getInstance().getPluginConfiguration().inviteMembers)));
            return;
        }

        if (!UserUtils.playedBefore(args[0])) {
            player.sendMessage(messages.generalNotPlayedBefore);
            return;
        }

        User invitedUser = User.get(args[0]);
        Player invitedPlayer = invitedUser.getPlayer();

        if (InvitationList.hasInvitationFrom(invitedUser, guild)) {
            if (!SimpleEventHandler.handle(new GuildMemberRevokeInviteEvent(EventCause.USER, user, guild, invitedUser))) {
                return;
            }
            
            InvitationList.expireInvitation(guild, invitedUser);
            player.sendMessage(messages.inviteCancelled);
            
            if (invitedPlayer != null) {
                invitedPlayer.sendMessage(messages.inviteCancelledToInvited.replace("{OWNER}", player.getName()).replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));
            }
            
            return;
        }

        if (invitedPlayer == null) {
            player.sendMessage(messages.invitePlayerExists);
            return;
        }

        if (invitedUser.hasGuild()) {
            player.sendMessage(messages.generalUserHasGuild);
            return;
        }

        if (!SimpleEventHandler.handle(new GuildMemberInviteEvent(EventCause.USER, user, guild, invitedUser))) {
            return;
        }
        
        InvitationList.createInvitation(guild, invitedPlayer);
        player.sendMessage(messages.inviteToOwner.replace("{PLAYER}", invitedPlayer.getName()));
        invitedPlayer.sendMessage(messages.inviteToInvited.replace("{OWNER}", player.getName()).replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));
    }

}

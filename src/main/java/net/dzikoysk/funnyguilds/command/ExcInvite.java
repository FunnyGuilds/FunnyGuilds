package net.dzikoysk.funnyguilds.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.util.InvitationList;

public class ExcInvite implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig messages = Messages.getInstance();
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

        if (guild.getMembers().size() >= Settings.getConfig().inviteMembers) {
            player.sendMessage(messages.inviteAmount.replace("{AMOUNT}", Integer.toString(Settings.getConfig().inviteMembers)));
            return;
        }

        if (!UserUtils.playedBefore(args[0])) {
            player.sendMessage(messages.generalNotPlayedBefore);
            return;
        }

        User invitedUser = User.get(args[0]);
        Player invitedPlayer = invitedUser.getPlayer();

        if (InvitationList.hasInvitationFrom(invitedUser, guild)) {
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

        InvitationList.createInvitation(guild, invitedPlayer);
        player.sendMessage(messages.inviteToOwner.replace("{PLAYER}", invitedPlayer.getName()));
        invitedPlayer.sendMessage(messages.inviteToInvited.replace("{OWNER}", player.getName()).replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));
    }

}

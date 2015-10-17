package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.OfflineUser;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.util.thread.ActionType;
import net.dzikoysk.funnyguilds.util.thread.IndependentThread;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcKick implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        Messages messages = Messages.getInstance();
        Player player = (Player) sender;
        User user = User.get(player);

        if (!user.hasGuild()) {
            player.sendMessage(messages.getMessage("kickHasNotGuild"));
            return;
        }

        if (!user.isOwner() && !user.isDeputy()) {
            player.sendMessage(messages.getMessage("kickIsNotOwner"));
            return;
        }

        if (args.length < 1) {
            player.sendMessage(messages.getMessage("kickPlayer"));
            return;
        }

        User kickedUser = User.get(args[0]);
        OfflineUser kickedOffline = kickedUser.getOfflineUser();

        if (!kickedUser.hasGuild()) {
            player.sendMessage(messages.getMessage("kickToHasNotGuild"));
            return;
        }

        if (!user.getGuild().equals(kickedUser.getGuild())) {
            player.sendMessage(messages.getMessage("kickOtherGuild"));
            return;
        }

        if (kickedUser.isOwner()) {
            player.sendMessage(messages.getMessage("kickOwner"));
            return;
        }

        Guild guild = user.getGuild();

        IndependentThread.action(ActionType.PREFIX_GLOBAL_REMOVE_PLAYER, kickedOffline);

        guild.removeMember(kickedUser);
        kickedUser.removeGuild();

        if (kickedOffline.isOnline())
            IndependentThread.action(ActionType.PREFIX_GLOBAL_UPDATE_PLAYER, player);

        player.sendMessage(
                messages.getMessage("kickToOwner")
                        .replace("{PLAYER}", kickedUser.getName()));

        Player kickedPlayer = Bukkit.getPlayer(kickedUser.getName());
        if (kickedPlayer != null)
            kickedPlayer.sendMessage(messages.getMessage("kickToPlayer")
                    .replace("{GUILD}", guild.getName()));

        Bukkit.broadcastMessage(messages.getMessage("broadcastKick")
                .replace("{PLAYER}", kickedUser.getName())
                .replace("{GUILD}", guild.getName())
                .replace("{TAG}", guild.getTag()));
    }

}

package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.util.thread.ActionType;
import net.dzikoysk.funnyguilds.util.thread.IndependentThread;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcLeave implements Executor {

    @Override
    public void execute(CommandSender s, String[] args) {
        Messages messages = Messages.getInstance();
        Player player = (Player) s;
        User user = User.get(player);

        if (!user.hasGuild()) {
            player.sendMessage(messages.getMessage("leaveHasNotGuild"));
            return;
        }

        if (user.isOwner()) {
            player.sendMessage(messages.getMessage("leaveIsOwner"));
            return;
        }

        Guild guild = user.getGuild();
        IndependentThread.action(ActionType.PREFIX_GLOBAL_REMOVE_PLAYER, user.getOfflineUser());
        guild.removeMember(user);
        user.removeGuild();
        IndependentThread.action(ActionType.PREFIX_GLOBAL_UPDATE_PLAYER, player);

        player.sendMessage(messages.getMessage("leaveToUser")
                .replace("{GUILD}", guild.getName())
                .replace("{TAG}", guild.getTag()));

        Bukkit.broadcastMessage(messages.getMessage("broadcastLeave")
                .replace("{PLAYER}", user.getName())
                .replace("{GUILD}", guild.getName())
                .replace("{TAG}", guild.getTag()));
    }
}

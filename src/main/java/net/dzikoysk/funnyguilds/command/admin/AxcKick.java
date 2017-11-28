package net.dzikoysk.funnyguilds.command.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.util.thread.ActionType;
import net.dzikoysk.funnyguilds.util.thread.IndependentThread;

public class AxcKick implements Executor {

    @Override
    public void execute(CommandSender s, String[] args) {
        MessagesConfig m = Messages.getInstance();

        if (args.length < 1) {
            s.sendMessage(m.adminNoNickGiven);
            return;
        }

        User user = User.get(args[0]);

        if (!user.hasGuild()) {
            s.sendMessage(m.adminNoGuild);
            return;
        }

        if (user.isOwner()) {
            s.sendMessage(m.adminGuildOwner);
            return;
        }

        IndependentThread.action(ActionType.PREFIX_GLOBAL_REMOVE_PLAYER, user.getOfflineUser());

        Guild guild = user.getGuild();
        guild.removeMember(user);
        user.removeGuild();

        Player p = user.getPlayer();
        if (p != null) {
            IndependentThread.action(ActionType.PREFIX_GLOBAL_UPDATE_PLAYER, p);
            p.sendMessage(m.kickToPlayer.replace("{GUILD}", guild.getName()));
        }

        s.sendMessage(m.kickToOwner.replace("{PLAYER}", user.getName()));
        Bukkit.broadcastMessage(m.broadcastKick.replace("{PLAYER}", user.getName()).replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));
    }
}

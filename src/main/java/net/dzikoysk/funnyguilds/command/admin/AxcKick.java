package net.dzikoysk.funnyguilds.command.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.util.thread.ActionType;
import net.dzikoysk.funnyguilds.util.thread.IndependentThread;

public class AxcKick implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig messages = Messages.getInstance();

        if (args.length < 1) {
            sender.sendMessage(messages.generalNoNickGiven);
            return;
        }
        
        if (!UserUtils.playedBefore(args[0])) {
            sender.sendMessage(messages.generalNotPlayedBefore);
            return;
        }

        User user = User.get(args[0]);
        if (!user.hasGuild()) {
            sender.sendMessage(messages.generalPlayerHasNoGuild);
            return;
        }

        if (user.isOwner()) {
            sender.sendMessage(messages.adminGuildOwner);
            return;
        }

        IndependentThread.action(ActionType.PREFIX_GLOBAL_REMOVE_PLAYER, user.getOfflineUser());

        Guild guild = user.getGuild();
        guild.removeMember(user);
        user.removeGuild();

        Player p = user.getPlayer();
        if (p != null) {
            IndependentThread.action(ActionType.PREFIX_GLOBAL_UPDATE_PLAYER, p);
            p.sendMessage(messages.kickToPlayer.replace("{GUILD}", guild.getName()));
        }

        sender.sendMessage(messages.kickToOwner.replace("{PLAYER}", user.getName()));
        Bukkit.broadcastMessage(messages.broadcastKick.replace("{PLAYER}", user.getName()).replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));
    }
}

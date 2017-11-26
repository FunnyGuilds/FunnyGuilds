package net.dzikoysk.funnyguilds.command.admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;

public class AxcSpy implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {

        MessagesConfig m = Messages.getInstance();
        Player player = (Player) sender;
        User user = User.get(player);

        if (!player.hasPermission("funnyguilds.admin")) {
            player.sendMessage(m.permission);
            return;
        }

        if (user.isSpy()) {
            user.setSpy(false);
            sender.sendMessage(ChatColor.RED + "Juz nie szpiegujesz graczy");
        } else {
            user.setSpy(true);
            sender.sendMessage(ChatColor.GREEN + "Od teraz szpiegujesz graczy");
        }
    }
}

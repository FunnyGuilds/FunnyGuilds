package net.dzikoysk.funnyguilds.command.admin;

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
        User user = User.get((Player) sender);

        if (user.isSpy()) {
            user.setSpy(false);
            sender.sendMessage(m.adminStopSpy);
        } else {
            user.setSpy(true);
            sender.sendMessage(m.adminStartSpy);
        }
    }
}

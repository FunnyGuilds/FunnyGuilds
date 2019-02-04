package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserCache;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AxcSpy implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
        UserCache cache = User.get((Player) sender).getCache();

        if (cache.isSpy()) {
            cache.setSpy(false);
            sender.sendMessage(messages.adminStopSpy);
        } else {
            cache.setSpy(true);
            sender.sendMessage(messages.adminStartSpy);
        }
    }

}

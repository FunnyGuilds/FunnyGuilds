package net.dzikoysk.funnyguilds.command.admin;

import org.bukkit.command.CommandSender;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;

public class AxcDeaths implements Executor {

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
        
        if (args.length < 2) {
            sender.sendMessage(messages.adminNoDeathsGiven);
            return;
        }

        int deaths;
        try {
            deaths = Integer.valueOf(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(messages.adminErrorInNumber.replace("{ERROR}", args[1]));
            return;
        }

        User user = User.get(args[0]);
        user.getRank().setDeaths(deaths);
        sender.sendMessage(messages.adminDeathsChanged.replace("{PLAYER}", user.getName()).replace("{DEATHS}", Integer.toString(deaths)));
    }

}

package net.dzikoysk.funnyguilds.command.admin;

import org.bukkit.command.CommandSender;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;

public class AxcDeaths implements Executor {

    @Override
    public void execute(CommandSender s, String[] args) {
        MessagesConfig m = Messages.getInstance();

        if (args.length < 1) {
            s.sendMessage(m.adminNoNickGiven);
            return;
        }

        if (args.length < 2) {
            s.sendMessage(m.adminNoDeathsGiven);
            return;
        }

        int deaths;
        try {
            deaths = Integer.valueOf(args[1]);
        } catch (NumberFormatException e) {
            s.sendMessage(m.adminErrorInNumber.replace("{ERROR}", args[1]));
            return;
        }

        User user = User.get(args[0]);
        user.getRank().setDeaths(deaths);
        s.sendMessage(m.adminDeathsChanged.replace("{PLAYER}", user.getName()).replace("{DEATHS}", Integer.toString(deaths)));
    }
}

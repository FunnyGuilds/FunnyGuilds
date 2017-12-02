package net.dzikoysk.funnyguilds.command.admin;

import org.bukkit.command.CommandSender;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.RankManager;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;

public class AxcPoints implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {

        MessagesConfig m = Messages.getInstance();

        if (args.length < 1) {
            sender.sendMessage(m.adminNoNickGiven);
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(m.adminNoPointsGiven);
            return;
        }

        int points;
        try {
            points = Integer.valueOf(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(m.adminErrorInNumber.replace("{ERROR}", args[1]));
            return;
        }

        User user = User.get(args[0]);
        user.getRank().setPoints(points);
        RankManager.getInstance().update(user);

        sender.sendMessage(m.adminPointsChanged.replace("{PLAYER}", user.getName()).replace("{POINTS}", Integer.toString(points)));
    }
}

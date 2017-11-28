package net.dzikoysk.funnyguilds.command.admin;

import org.bukkit.command.CommandSender;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;

public class AxcKills implements Executor {

    @Override
    public void execute(CommandSender s, String[] args) {
        MessagesConfig m = Messages.getInstance();

        if (args.length < 1) {
            s.sendMessage(m.adminNoNickGiven);
            return;
        }

        if (args.length < 2) {
            s.sendMessage(m.adminNoKillsGiven);
            return;
        }

        int kills;
        try {
            kills = Integer.valueOf(args[1]);
        } catch (NumberFormatException e) {
            s.sendMessage(m.adminErrorInNumber.replace("{ERROR}", args[1]));
            return;
        }

        User user = User.get(args[0]);
        user.getRank().setKills(kills);
        s.sendMessage(m.adminKillsChanged.replace("{PLAYER}", user.getName()).replace("{KILLS}", Integer.toString(kills)));
    }
}

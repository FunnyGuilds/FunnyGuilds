package net.dzikoysk.funnyguilds.command.admin;

import org.bukkit.command.CommandSender;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;

public class AxcLives implements Executor {

    @Override
    public void execute(CommandSender s, String[] args) {
        MessagesConfig m = Messages.getInstance();

        if (args.length < 1) {
            s.sendMessage(m.adminNoTagGiven);
            return;
        }

        if (args.length < 2) {
            s.sendMessage(m.adminNoLivesGiven);
            return;
        }

        Guild guild = GuildUtils.byTag(args[0]);
        if (guild == null) {
            s.sendMessage(m.adminNoGuildFound);
            return;
        }

        int lives;
        try {
            lives = Integer.valueOf(args[1]);
        } catch (NumberFormatException e) {
            s.sendMessage(m.adminErrorInNumber.replace("{ERROR}", args[1]));;
            return;
        }

        guild.setLives(lives);
        s.sendMessage(m.adminLivesChanged.replace("{GUILD}", guild.getTag()).replace("{LIVES}", Integer.toString(lives)));
    }
}

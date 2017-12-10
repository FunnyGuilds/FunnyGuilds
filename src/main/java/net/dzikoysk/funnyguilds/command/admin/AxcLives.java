package net.dzikoysk.funnyguilds.command.admin;

import org.bukkit.command.CommandSender;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;

public class AxcLives implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig messages = Messages.getInstance();

        if (args.length < 1) {
            sender.sendMessage(messages.generalNoTagGiven);
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(messages.adminNoLivesGiven);
            return;
        }

        if (!GuildUtils.tagExists(args[0])) {
            sender.sendMessage(messages.generalNoGuildFound);
            return;
        }
        
        Guild guild = GuildUtils.byTag(args[0]);

        int lives;
        try {
            lives = Integer.valueOf(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(messages.adminErrorInNumber.replace("{ERROR}", args[1]));;
            return;
        }

        guild.setLives(lives);
        sender.sendMessage(messages.adminLivesChanged.replace("{GUILD}", guild.getTag()).replace("{LIVES}", Integer.toString(lives)));
    }
}

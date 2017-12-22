package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.util.Parser;
import org.bukkit.command.CommandSender;

import java.util.Date;

public class AxcValidity implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig messages = Messages.getInstance();
        PluginConfig config = Settings.getConfig();

        if (args.length < 1) {
            sender.sendMessage(messages.generalNoTagGiven);
            return;
        } else if (args.length < 2) {
            sender.sendMessage(messages.adminNoValidityTimeGiven);
            return;
        }

        Guild guild = GuildUtils.byTag(args[0]);

        if (guild == null) {
            sender.sendMessage(messages.generalNoGuildFound);
            return;
        }

        if (guild.isBanned()) {
            sender.sendMessage(messages.adminGuildBanned);
            return;
        }

        long time = Parser.parseTime(args[1]);

        if (time < 1) {
            sender.sendMessage(messages.adminTimeError);
            return;
        }

        long validity = guild.getValidity();

        if (validity == 0) {
            validity = System.currentTimeMillis();
        }
        
        validity += time;
        guild.setValidity(validity);

        String date = config.dateFormat.format(new Date(validity));
        sender.sendMessage(messages.adminNewValidity.replace("{GUILD}", guild.getName()).replace("{VALIDITY}", date));
    }

}

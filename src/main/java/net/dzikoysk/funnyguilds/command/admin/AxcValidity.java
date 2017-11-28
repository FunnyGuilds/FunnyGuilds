package net.dzikoysk.funnyguilds.command.admin;

import java.util.Date;

import org.bukkit.command.CommandSender;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.util.Parser;

public class AxcValidity implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig m = Messages.getInstance();

        if (args.length < 1) {
            sender.sendMessage(m.adminNoTagGiven);
            return;
        } else if (args.length < 2) {
            sender.sendMessage(m.adminNoValidityTimeGiven);
            return;
        }

        if (!GuildUtils.tagExists(args[0])) {
            sender.sendMessage(m.adminNoGuildFound);
            return;
        }

        Guild guild = GuildUtils.byTag(args[0]);
        if (guild.isBanned()) {
            sender.sendMessage(m.adminGuildBanned);
            return;
        }

        long time = Parser.parseTime(args[1]);
        if (time < 1) {
            sender.sendMessage(m.adminTimeError);
            return;
        }

        long c = guild.getValidity();
        if (c == 0) {
            c = System.currentTimeMillis();
        }
        
        c += time;
        guild.setValidity(c);

        sender.sendMessage(m.adminNewValidity.replace("{GUILD}", guild.getName()).replace("{VALIDITY}", Settings.getConfig().dateFormat.format(new Date(c))));
    }
}

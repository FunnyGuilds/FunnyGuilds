package net.dzikoysk.funnyguilds.command.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.system.ban.BanUtils;
import net.dzikoysk.funnyguilds.util.Parser;
import net.dzikoysk.funnyguilds.util.StringUtils;

public class AxcBan implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig messages = Messages.getInstance();

        if (args.length < 1) {
            sender.sendMessage(messages.generalNoTagGiven);
            return;
        } else if (args.length < 2) {
            sender.sendMessage(messages.adminNoBanTimeGiven);
            return;
        } else if (args.length < 3) {
            sender.sendMessage(messages.adminNoReasonGiven);
            return;
        }

        if (!GuildUtils.tagExists(args[0])) {
            sender.sendMessage(messages.generalNoGuildFound);
            return;
        } 
        
        Guild guild = GuildUtils.byTag(args[0]);
        if (guild.isBanned()) {
            sender.sendMessage(messages.adminGuildBanned);
            return;
        }

        long time = Parser.parseTime(args[1]);
        if (time < 1) {
            sender.sendMessage(messages.adminTimeError);
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            sb.append(args[i]);
            sb.append(" ");
        }
        
        String reason = sb.toString();
        
        BanUtils.ban(guild, time, reason);
        sender.sendMessage(messages.adminGuildBan.replace("{GUILD}", guild.getName()).replace("{TIME}", args[1]));
        Bukkit.broadcastMessage(Messages.getInstance().broadcastBan.replace("{GUILD}", guild.getName())
                        .replace("{TAG}", guild.getTag()).replace("{REASON}", StringUtils.colored(reason)).replace("{TIME}", args[1]));
    }
}

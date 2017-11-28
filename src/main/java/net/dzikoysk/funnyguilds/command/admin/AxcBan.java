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
    public void execute(CommandSender s, String[] args) {
        MessagesConfig m = Messages.getInstance();

        if (args.length < 1) {
            s.sendMessage(m.adminNoTagGiven);
            return;
        } else if (args.length < 2) {
            s.sendMessage(m.adminNoBanTimeGiven);
            return;
        } else if (args.length < 3) {
            s.sendMessage(m.adminNoReasonGiven);
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            sb.append(args[i]);
            sb.append(" ");
        }
        
        String reason = sb.toString();
        
        if (!GuildUtils.tagExists(args[0])) {
            s.sendMessage(m.adminNoGuildFound);
            return;
        }

        Guild guild = GuildUtils.byTag(args[0]);
        if (guild.isBanned()) {
            s.sendMessage(m.adminGuildBanned);
            return;
        }

        long time = Parser.parseTime(args[1]);
        if (time < 1) {
            s.sendMessage(m.adminTimeError);
            return;
        }

        BanUtils.ban(guild, time, reason);
        s.sendMessage(m.adminGuildBan.replace("{GUILD}", guild.getName()).replace("{TIME}", args[1]));
        Bukkit.broadcastMessage(Messages.getInstance().broadcastBan.replace("{GUILD}", guild.getName())
                        .replace("{TAG}", guild.getTag()).replace("{REASON}", StringUtils.colored(reason)).replace("{TIME}", args[1]));
    }
}

package net.dzikoysk.funnyguilds.command.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.system.ban.BanUtils;

public class AxcUnban implements Executor {

    @Override
    public void execute(CommandSender s, String[] args) {
        MessagesConfig m = Messages.getInstance();

        if (args.length < 1) {
            s.sendMessage(m.adminNoTagGiven);
            return;
        }

        String tag = args[0];
        if (!GuildUtils.tagExists(tag)) {
            s.sendMessage(m.adminNoGuildFound);
            return;
        }

        Guild guild = GuildUtils.byTag(tag);
        if (!guild.isBanned()) {
            s.sendMessage(m.adminGuildNotBanned);
            return;
        }

        BanUtils.unban(guild);
        s.sendMessage(m.adminGuildUnban.replace("{GUILD}", guild.getName()));
        Bukkit.broadcastMessage(Messages.getInstance().broadcastUnban.replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));
    }
}

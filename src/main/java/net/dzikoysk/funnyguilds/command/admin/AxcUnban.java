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
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig messages = Messages.getInstance();

        if (args.length < 1) {
            sender.sendMessage(messages.generalNoTagGiven);
            return;
        }

        if (!GuildUtils.tagExists(args[0])) {
            sender.sendMessage(messages.generalNoGuildFound);
            return;
        }

        Guild guild = GuildUtils.byTag(args[0]);
        if (!guild.isBanned()) {
            sender.sendMessage(messages.adminGuildNotBanned);
            return;
        }

        BanUtils.unban(guild);
        sender.sendMessage(messages.adminGuildUnban.replace("{GUILD}", guild.getName()));
        Bukkit.broadcastMessage(Messages.getInstance().broadcastUnban.replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));
    }
}

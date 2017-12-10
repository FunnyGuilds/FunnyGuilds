package net.dzikoysk.funnyguilds.command.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;

public class AxcDelete implements Executor {

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
        GuildUtils.deleteGuild(guild);
        sender.sendMessage(messages.deleteSuccessful.replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));

        Player owner = guild.getOwner().getPlayer();
        if (owner != null) {
            owner.sendMessage(messages.adminGuildBroken.replace("{ADMIN}", sender.getName()));
        }

        Bukkit.getServer().broadcastMessage(messages.broadcastDelete.replace("{PLAYER}", sender.getName()).replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));
    }
}

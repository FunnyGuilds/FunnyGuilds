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
    public void execute(CommandSender s, String[] args) {
        MessagesConfig m = Messages.getInstance();

        if (args.length < 1) {
            s.sendMessage(m.adminNoTagGiven);
            return;
        }

        if (!GuildUtils.tagExists(args[0])) {
            s.sendMessage(m.adminNoGuildFound);
            return;
        }

        Guild guild = GuildUtils.byTag(args[0]);
        GuildUtils.deleteGuild(guild);
        s.sendMessage(m.deleteSuccessful.replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));

        Player owner = guild.getOwner().getPlayer();
        if (owner != null) {
            owner.sendMessage(m.adminGuildBroken.replace("{ADMIN}", s.getName()));
        }

        Bukkit.getServer().broadcastMessage(m.broadcastDelete.replace("{PLAYER}", s.getName()).replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));
    }
}

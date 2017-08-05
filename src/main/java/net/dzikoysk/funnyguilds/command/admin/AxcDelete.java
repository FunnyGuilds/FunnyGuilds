package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AxcDelete implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {

        MessagesConfig m = Messages.getInstance();
        Player player = (Player) sender;

        if (!player.hasPermission("funnyguilds.admin")) {
            player.sendMessage(m.permission);
            return;
        }

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Podaj tag gildii!");
            return;
        }

        String tag = args[0];

        if (!GuildUtils.tagExists(tag)) {
            player.sendMessage(ChatColor.RED + "Nie ma gildii o takim tagu!");
            return;
        }

        Guild guild = GuildUtils.byTag(tag);

        User o = guild.getOwner();
        String name = guild.getName();
        tag = guild.getTag();

        GuildUtils.deleteGuild(guild);

        player.sendMessage(m.deleteSuccessful
                                   .replace("{GUILD}", name)
                                   .replace("{TAG}", tag)
        );

        Player owner = Bukkit.getPlayer(o.getName());
        if (owner != null) {
            owner.sendMessage(
                    ChatColor.RED + "Twoja gildia zostala rozwiazana przez " + ChatColor.GRAY + player.getDisplayName()
            );
        }

        Bukkit.getServer().broadcastMessage(m.broadcastDelete
                                                    .replace("{PLAYER}", player.getDisplayName())
                                                    .replace("{GUILD}", name)
                                                    .replace("{TAG}", tag)
        );
    }

}

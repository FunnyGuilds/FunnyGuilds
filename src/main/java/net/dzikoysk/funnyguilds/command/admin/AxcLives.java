package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AxcLives implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        Messages m = Messages.getInstance();
        Player player = (Player) sender;

        if (!player.hasPermission("funnyguilds.admin")) {
            player.sendMessage(m.getMessage("permission"));
            return;
        }

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Podaj tag gildii!");
            return;
        }

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Podaj ilosc zyc!");
            return;
        }

        Guild guild = GuildUtils.byTag(args[0]);
        if (guild == null) {
            player.sendMessage(ChatColor.RED + "Nie ma gildii o takim tagu!");
            return;
        }

        int lives = 0;
        try {
            lives = Integer.valueOf(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Nieprawidlowa ilosc zyc! Nieznana jest liczba: " + ChatColor.DARK_RED + args[1]);
            return;
        }

        guild.setLives(lives);
        player.sendMessage(ChatColor.GRAY + "Ustawiono " + ChatColor.AQUA + lives + " zyc " + ChatColor.GRAY + "dla gildii " + ChatColor.AQUA + guild.getTag());
        return;
    }

}

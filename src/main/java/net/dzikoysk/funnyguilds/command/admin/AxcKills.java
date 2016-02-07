package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AxcKills implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        Messages m = Messages.getInstance();
        Player player = (Player) sender;

        if (!player.hasPermission("funnyguilds.admin")) {
            player.sendMessage(m.getMessage("permission"));
            return;
        }

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Podaj nick gracza!");
            return;
        }

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Podaj ilosc zabojstw!");
            return;
        }

        int kills = 0;
        try {
            kills = Integer.valueOf(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Nieprawidlowa ilosc zabojstw! Nieznana jest liczba: " + ChatColor.DARK_RED + args[1]);
            return;
        }

        User user = User.get(args[0]);
        user.getRank().setKills(kills);
        player.sendMessage(ChatColor.GRAY + "Ustawiono " + ChatColor.AQUA + kills + " zabojstw " + ChatColor.GRAY + "dla " + ChatColor.AQUA + user.getName());
        return;
    }

}

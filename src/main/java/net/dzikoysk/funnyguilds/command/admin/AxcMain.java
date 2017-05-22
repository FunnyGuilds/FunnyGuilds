package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AxcMain implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {

        MessagesConfig m = Messages.getInstance();
        Player player = (Player) sender;

        if (!player.hasPermission("funnyguilds.admin")) {
            player.sendMessage(m.permission);
            return;
        }

        final PluginConfig.Commands commands = Settings.getConfig().commands;
        player.sendMessage(ChatColor.AQUA + "/" + commands.admin.add + " [tag] [nick] " + ChatColor.GRAY + "- Dodaje gracza do gildii");
        player.sendMessage(ChatColor.AQUA + "/" + commands.admin.delete + " [tag] " + ChatColor.GRAY + "- Usuwa gildie");
        player.sendMessage(ChatColor.AQUA + "/" + commands.admin.kick + " [nick] " + ChatColor.GRAY + "- Wyrzuca gracza z gildii");
        player.sendMessage(ChatColor.AQUA + "/" + commands.admin.teleport + " [tag] " + ChatColor.GRAY + "- Teleportuje do bazy gildii");
        player.sendMessage(ChatColor.AQUA + "/" + commands.admin.points + " [nick] [points] " + ChatColor.GRAY + "- Ustawia punkty gracza");
        player.sendMessage(ChatColor.AQUA + "/" + commands.admin.kills + " [nick] [kills] " + ChatColor.GRAY + "- Ustawia ilosc zabojstw gracza");
        player.sendMessage(ChatColor.AQUA + "/" + commands.admin.deaths + " [nick] [deaths] " + ChatColor.GRAY + "- Ustawia ilosc smierci gracza");
        player.sendMessage(ChatColor.AQUA + "/" + commands.admin.ban + " [tag] [czas] [powod] " + ChatColor.GRAY + "- Banuje gildie na okreslony czas");
        player.sendMessage(ChatColor.AQUA + "/" + commands.admin.unban + " [tag] " + ChatColor.GRAY + "- Odbanowywuje gildie");
        player.sendMessage(ChatColor.AQUA + "/" + commands.admin.lives + " [tag] [zycia] " + ChatColor.GRAY + "- Ustawia ilosc zyc gildii");
        player.sendMessage(ChatColor.AQUA + "/" + commands.admin.move + " [tag]" + ChatColor.GRAY + "- Przenosi teren gildii");
        player.sendMessage(ChatColor.AQUA + "/" + commands.admin.validity + " [tag] [czas] " + ChatColor.GRAY + "- Przedluza waznosc gildii o podany czas");
        player.sendMessage(ChatColor.AQUA + "/" + commands.admin.name + " [tag] [nazwa] " + ChatColor.GRAY + "- Zmienia nazwe gildii");
    }
}

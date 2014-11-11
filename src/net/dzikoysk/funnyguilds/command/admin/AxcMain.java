package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.Messages;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AxcMain implements Executor {

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		Messages m = Messages.getInstance();
		Player player = (Player) sender;
		
		if(!player.hasPermission("funnyguilds.admin")){
			player.sendMessage(m.getMessage("permission"));
			return;
		}
		
		Settings c = Settings.getInstance();
		player.sendMessage(ChatColor.AQUA + "/" + c.axcAdd + " [tag] [nick] " + ChatColor.GRAY + "- Dodaje gracza do gildii");
		player.sendMessage(ChatColor.AQUA + "/" + c.axcDelete + " [tag] " + ChatColor.GRAY + "- Usuwa gildie");
		player.sendMessage(ChatColor.AQUA + "/" + c.axcKick + " [nick] " + ChatColor.GRAY + "- Wyrzuca gracza z gildii");
		player.sendMessage(ChatColor.AQUA + "/" + c.axcTeleport + " [tag] " + ChatColor.GRAY + "- Teleportuje do bazy gildii");
		player.sendMessage(ChatColor.AQUA + "/" + c.axcPoints + " [nick] [points] " + ChatColor.GRAY + "- Ustawia punkty gracza");
		player.sendMessage(ChatColor.AQUA + "/" + c.axcKills + " [nick] [kills] " + ChatColor.GRAY + "- Ustawia ilosc zabojstw gracza");
		player.sendMessage(ChatColor.AQUA + "/" + c.axcDeaths + " [nick] [deaths] " + ChatColor.GRAY + "- Ustawia ilosc smierci gracza");
		player.sendMessage(ChatColor.AQUA + "/" + c.axcBan + " [tag] [czas] " + ChatColor.GRAY + "- Banuje gildie na okreslony czas");
		player.sendMessage(ChatColor.AQUA + "/" + c.axcLives + " [tag] [zycia] " + ChatColor.GRAY + "- Ustawia ilosc zyc gildii");
	}
}

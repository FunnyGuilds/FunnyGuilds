package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AxcGuild implements Executor {

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		Messages m = Messages.getInstance();
		Player player = (Player) sender;
		
		if(!player.hasPermission("funnyguilds.admin")){
			player.sendMessage(m.getMessage("permission"));
			return;
		}
		
		player.sendMessage(ChatColor.AQUA + "/ga dodaj [tag] [nick] " + ChatColor.GRAY + "- Dodaje gracza do gildii");
		player.sendMessage(ChatColor.AQUA + "/ga usun [tag] " + ChatColor.GRAY + "- Usuwa gildie");
		player.sendMessage(ChatColor.AQUA + "/ga wyrzuc [nick] " + ChatColor.GRAY + "- Wyrzuca gracza z gildii");
		player.sendMessage(ChatColor.AQUA + "/ga tp [tag] " + ChatColor.GRAY + "- Teleportuje do bazy gildii");
		
	}
}

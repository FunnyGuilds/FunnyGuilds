package net.dzikoysk.funnyguilds.command.admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;

public class AxcPoints implements Executor {

	@Override
	public void execute(CommandSender sender, String[] args) {
		Messages m = Messages.getInstance();
		Player player = (Player) sender;
		
		if(!player.hasPermission("funnyguilds.admin")){
			player.sendMessage(m.getMessage("permission"));
			return;
		}
		
		if(args.length < 1){
			player.sendMessage(ChatColor.RED + "Podaj nick gracza!");
			return;
		}
		
		if(args.length < 2){
			player.sendMessage(ChatColor.RED + "Podaj ilosc punktow!");
			return;
		}
		
		int points = 0;
		try {
			points = Integer.valueOf(args[1]);
		} catch (NumberFormatException e){
			player.sendMessage(ChatColor.RED + "Nieprawidlowa ilosc punktow! Nieznana jest liczba: " + ChatColor.DARK_RED + args[1]);
			return;
		}

		User user = User.get(args[0]);
		user.getRank().setPoints(points);
		player.sendMessage(ChatColor.GRAY + "Ustawiono " + ChatColor.AQUA + points + " punktow " + ChatColor.GRAY + "dla " + ChatColor.AQUA + user.getName());
		return;
	}

}

package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Config;
import net.dzikoysk.funnyguilds.data.DataManager;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.util.Version;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcFunnyGuilds implements Executor {

	public void execute(final CommandSender s, String[] args){
		if(args.length > 0){
			if(args[0].equalsIgnoreCase("reload")){
				if(s instanceof Player){
					if(!s.hasPermission("funnyguilds.reload")){
						s.sendMessage(Messages.getInstance().getMessage("permission"));
						return;
					}
				}
				Thread thread = new Thread(){
					@Override
					public void run(){
						DataManager dm = DataManager.getInstance();
						dm.stop();
						dm.save();
						new Messages();
						new Config();
						dm.start();
						s.sendMessage(ChatColor.AQUA + "FunnyGuilds " + ChatColor.GRAY + "przeladowano!");
					}
				};
				s.sendMessage(ChatColor.GRAY + "Przeladowywanie...");
				thread.start();
				return;
			}
			if(args[0].equalsIgnoreCase("check") || args[0].equalsIgnoreCase("update")){
				if(s instanceof Player) Version.check((Player)s);
				else FunnyGuilds.info("Console can not use this command");
				return;
			}
			if(args[0].equalsIgnoreCase("admin") || args[0].equalsIgnoreCase("zarzadzaj")){
				
				return;
			}
		}
		s.sendMessage(ChatColor.GRAY + "FunnyGuilds " + ChatColor.AQUA + FunnyGuilds.getVersion() + ChatColor.GRAY + " by " + ChatColor.AQUA + "Dzikoysk");
	}
	
}

package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AxcTeleport implements Executor {

	@Override
	public void execute(CommandSender sender, String[] args) {
		Messages messages = Messages.getInstance();
		Player player = (Player) sender;
		
		if(!player.hasPermission("funnyguilds.admin")){
			player.sendMessage(messages.getMessage("permission"));
			return;
		}
		
		if(args.length < 1){
			player.sendMessage(ChatColor.RED + "Podaj tag gildii!");
			return;
		}
		
		String tag = args[0];
		if(!GuildUtils.tagExists(tag)){
			player.sendMessage(ChatColor.RED + "Nie ma gildii o takim tagu!");
			return;
		}
		
		Region region = GuildUtils.byTag(tag).getRegion();
		if(region == null || region.getCenter() == null){
			player.sendMessage(ChatColor.RED + "Gildia nie posiada terenu!");
			return;
		}
		
		player.sendMessage(ChatColor.AQUA + "Teleportacja" + ChatColor.GRAY + "...");
		player.teleport(region.getCenter());
	}

}

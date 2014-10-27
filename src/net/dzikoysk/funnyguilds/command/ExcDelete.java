package net.dzikoysk.funnyguilds.command;

import java.util.ArrayList;
import java.util.UUID;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.data.Config;
import net.dzikoysk.funnyguilds.data.DataManager;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.util.ActionType;
import net.dzikoysk.funnyguilds.util.IndependentThread;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcDelete extends Exc {
	
	public static ArrayList<UUID> confirm = new ArrayList<UUID>();
	
	public ExcDelete(String command, String perm){
		super(command, perm);
		this.register();
	}
	
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args){
		   if (cmd.getName().equalsIgnoreCase(Config.getInstance().excDelete)) {
			   if(!(s instanceof Player)){
				   FunnyGuilds.info("Console can not use this command");
	    			return false;
			   }
			   
			   Messages m = Messages.getInstance();
			   
			   Player p = (Player) s;
			   User lp = User.get(p);
			   
			   if(!lp.hasGuild()){
				   p.sendMessage(m.getMessage("deleteHasNotGuild"));
				   return true;
			   }
			   
			   if(!lp.isOwner()){
				   p.sendMessage(m.getMessage("deleteIsNotOwner"));
				   return true;
			   }
			   
			   confirm.add(lp.getUUID());
			   p.sendMessage(m.getMessage("deleteConfirm"));
		       return true;
		       
		   } else if (cmd.getName().equalsIgnoreCase(Config.getInstance().excConfirm)){
			   if(!(s instanceof Player)){
				   FunnyGuilds.info("Console can not use this command");
				   return true;
			   }
			  
			   Messages m = Messages.getInstance();
			   
			   Player p = (Player) s;
			   User lp = User.get(p.getUniqueId());
			   
			   if(!lp.hasGuild()){
				   p.sendMessage(m.getMessage("deleteHasNotGuild"));
				   return true;
			   }
			   
			   if(!lp.isOwner()){
				   p.sendMessage(m.getMessage("deleteIsNotOwner"));
				   return true;
			   }
			   
			   if(!confirm.contains(lp.getUUID())){
				   p.sendMessage(m.getMessage("deleteToConfirm"));
				   return true;
			   }
			   
			   DataManager.getInstance().stop();
			   confirm.remove(lp.getUUID());
			   
			   Guild guild = lp.getGuild();
			   String name = guild.getName();
			   String tag = guild.getTag();
			   
			   Region region = Region.get(guild.getRegion());
			   Block block = region.getCenter().getBlock().getRelative(BlockFace.DOWN);
			   if(block.getLocation().getBlockY() > 1) block.setType(Material.AIR);
			   
			   
			   IndependentThread.action(ActionType.PREFIX_GLOBAL_REMOVE_GUILD, guild);
			   GuildUtils.deleteGuild(guild);
			   DataManager.getInstance().start();
			   
			   p.sendMessage(
				   m.getMessage("deleteSuccessful")
				   .replace("{GUILD}", name)
				   .replace("{TAG}", tag)
			   );
			   
			   Bukkit.getServer().broadcastMessage(
				   m.getMessage("broadcastDelete")
				   .replace("{PLAYER}", p.getName())
				   .replace("{GUILD}", name)
				   .replace("{TAG}", tag)
			   );
			   return true;
		   }
		   return false;
	}


}
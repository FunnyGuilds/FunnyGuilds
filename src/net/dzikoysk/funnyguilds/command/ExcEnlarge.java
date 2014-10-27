package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.data.Config;
import net.dzikoysk.funnyguilds.data.Messages;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ExcEnlarge extends Exc {
	
	public ExcEnlarge(String command, String perm){
		super(command, perm);
		this.register();
	}
	
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args){
	    if(!cmd.getName().equalsIgnoreCase(Config.getInstance().excEnlarge) || !Config.getInstance().enlargeEnable) return false;
	    
	    if(!(s instanceof Player)){
			FunnyGuilds.info("Console can not use this command");
			return true;
		}
	    
	    Messages m = Messages.getInstance();
		
		Player p = (Player)s;
		User lp = User.get(p);
		
		if(!lp.hasGuild()){
			p.sendMessage(m.getMessage("enlargeHasNotGuild"));
			return true;
		}
		
		if(!lp.isOwner()){
			p.sendMessage(m.getMessage("enlargeIsNotOwner"));
			return true;
		}
		
		Region region = Region.get(lp.getGuild().getRegion());
		int enlarge = region.getEnlarge();
		
		Config c = Config.getInstance();
		
		if(enlarge > c.enlargeItems.size()-1){
			p.sendMessage(m.getMessage("enlargeMaxSize"));
			return true;
		}
		
		ItemStack need = c.enlargeItems.get(enlarge);
		if(!p.getInventory().containsAtLeast(need, need.getAmount())){
			p.sendMessage(
				m.getMessage("enlargeItem")
				.replace("{ITEM}", need.getAmount() + " " + need.getType().name().toLowerCase())
			);
			return true;
		}
		
		p.getInventory().removeItem(need);
		region.setEnlarge(enlarge + 1);
		region.setSize(region.getSize() + c.enlargeSize);
		
		String tm = m.getMessage("enlargeDone")
		.replace("{SIZE}", region.getSize()+"")
		.replace("{LEVEL}", region.getEnlarge()+"");
		
		for(User user : lp.getGuild().getMembers()){
			OfflinePlayer of = Bukkit.getOfflinePlayer(user.getName());
			if(of.isOnline()){
				of.getPlayer().sendMessage(tm);
			}
		}
		
	    return true;
	}

}

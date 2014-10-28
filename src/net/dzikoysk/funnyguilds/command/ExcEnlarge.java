package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Config;
import net.dzikoysk.funnyguilds.data.Messages;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ExcEnlarge implements Executor {
	
	@Override
	public void execute(CommandSender s, String[] args){
	    Messages m = Messages.getInstance();
		Player p = (Player)s;
		User lp = User.get(p);
		
		if(!lp.hasGuild()){
			p.sendMessage(m.getMessage("enlargeHasNotGuild"));
			return;
		}
		
		if(!lp.isOwner()){
			p.sendMessage(m.getMessage("enlargeIsNotOwner"));
			return;
		}
		
		Region region = Region.get(lp.getGuild().getRegion());
		int enlarge = region.getEnlarge();
		
		Config c = Config.getInstance();
		
		if(enlarge > c.enlargeItems.size()-1){
			p.sendMessage(m.getMessage("enlargeMaxSize"));
			return;
		}
		
		ItemStack need = c.enlargeItems.get(enlarge);
		if(!p.getInventory().containsAtLeast(need, need.getAmount())){
			p.sendMessage(
				m.getMessage("enlargeItem")
				.replace("{ITEM}", need.getAmount() + " " + need.getType().name().toLowerCase())
			);
			return;
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
	    return;
	}

}

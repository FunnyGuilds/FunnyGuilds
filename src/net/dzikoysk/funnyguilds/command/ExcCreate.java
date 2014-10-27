package net.dzikoysk.funnyguilds.command;

import java.util.ArrayList;
import java.util.List;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Config;
import net.dzikoysk.funnyguilds.data.DataManager;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.util.ActionType;
import net.dzikoysk.funnyguilds.util.IndependentThread;
import net.dzikoysk.funnyguilds.util.StringUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ExcCreate implements Executor {
	
	public void execute(final CommandSender s, String[] args){
		
		Messages m = Messages.getInstance();
		
		Player p = (Player)s;
		User u = User.get(p);
		
		if(u.hasGuild()){
			p.sendMessage(m.getMessage("createHasGuild"));
			return;
		}
		
		if(!(args.length == 2)){ 
    		if(args.length == 0){
    			p.sendMessage(m.getMessage("createTag"));
    			return;
    		} else if(args.length == 1){
    			p.sendMessage(m.getMessage("createName"));
    			return;
    		} else if(args.length > 2){
    			p.sendMessage(m.getMessage("createMore"));
    			return;
    		}
		}
		
		Config c = Config.getInstance();
		
		String tag = args[0];
		String name = args[1];
		
		if(tag.length() > c.createTagLength){
			p.sendMessage(m.getMessage("createTagLength")
				.replace("{LENGTH}", Integer.toString(c.createTagLength)));
			return;
		}
		
		if(tag.length() < c.createTagMinLength){
			p.sendMessage(m.getMessage("createTagMinLength")
				.replace("{LENGTH}", Integer.toString(c.createTagMinLength)));
			return;
		}
		
		if(name.length() > c.createNameLength){
			p.sendMessage(m.getMessage("createNameLength")
				.replace("{LENGTH}", Integer.toString(c.createNameLength))
			);
			return;
		}
		
		if(name.length() < c.createNameMinLength){
			p.sendMessage(m.getMessage("createNameMinLength")
				.replace("{LENGTH}", Integer.toString(c.createNameMinLength))
			);
			return;
		}
		
		if(!tag.matches("[a-zA-Z]+")){
			p.sendMessage(m.getMessage("createOLTag"));
			return;
		}
		
		if(!name.matches("[a-zA-Z]+")){
			p.sendMessage(m.getMessage("createOLName"));
			return;
		}
		
		if(GuildUtils.isExists(name)){
			p.sendMessage(m.getMessage("createNameExists"));
			return;
		}
		
		if(GuildUtils.tagExists(tag)){
			p.sendMessage(m.getMessage("createTagExists"));
    		return;
    	}
		
		Location loc = p.getLocation();
		
		int d = Config.getInstance().regionSize + Config.getInstance().createDistance;
		if(Config.getInstance().enlargeItems != null) d = Config.getInstance().enlargeItems.size()*Config.getInstance().enlargeSize + d;
		
		if(d > p.getWorld().getSpawnLocation().distance(loc)){
			p.sendMessage(m.getMessage("createSpawn")
				.replace("{DISTANCE}", Integer.toString(d))
			);
			return;
		}
		
		if(RegionUtils.isNear(loc)){
			p.sendMessage(m.getMessage("createIsNear"));
			return;
		}
 
		List<ItemStack> itemsList = Config.getInstance().createItems;
		ItemStack[] items = itemsList.toArray(new ItemStack[0]); 
		for(int i = 0; i < items.length; i++){
			if(!p.getInventory().containsAtLeast(items[i], items[i].getAmount())){
				String msg = m.getMessage("createItems");
				if(msg.contains("{ITEM}")){
					StringBuilder sb = new StringBuilder();
					sb.append(items[i].getAmount());
					sb.append(" ");
					sb.append(items[i].getType().toString().toLowerCase());
					msg = msg.replace("{ITEM}", sb.toString());
				}
				if(msg.contains("{ITEMS}")){
					ArrayList<String> list = new ArrayList<String>();
					for(ItemStack it : itemsList){
						StringBuilder sb = new StringBuilder();
						sb.append(it.getAmount());
						sb.append(" ");
						sb.append(it.getType().toString().toLowerCase());
						list.add(sb.toString());
					}
					msg = msg.replace("{ITEMS}", StringUtils.toString(list, true));
				}
				p.sendMessage(msg);
				return;
			}
		}
		p.getInventory().removeItem(items);
		
		DataManager.getInstance().stop();
		
		Guild guild = new Guild(name);
		guild.setTag(tag);
		guild.setOwner(u);
		
		Region region = new Region(guild, loc, c.regionSize);
		guild.setRegion(region.getName());
		guild.addRegion(region.getName());
		
		u.setGuild(guild);
		
		loc.getBlock().getRelative(BlockFace.DOWN).setType(c.createMaterial);
		
		DataManager.getInstance().start();
		
		IndependentThread.actions(ActionType.RANK_UPDATE_GUILD, guild);
		IndependentThread.actions(ActionType.PREFIX_GLOBAL_ADD_GUILD, guild);
		IndependentThread.action(ActionType.PREFIX_GLOBAL_ADD_PLAYER, p);
		
		p.sendMessage(
			m.getMessage("createGuild")
			.replace("{GUILD}", name)
			.replace("{PLAYER}", p.getName())
			.replace("{TAG}", tag)
		);
		
		Bukkit.getServer().broadcastMessage(
			m.getMessage("broadcastCreate")
			.replace("{GUILD}", name)
			.replace("{PLAYER}", p.getName())
			.replace("{TAG}", tag)
		);
		return;
	}

}

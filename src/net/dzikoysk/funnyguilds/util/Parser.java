package net.dzikoysk.funnyguilds.util;

import java.util.Stack;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.RankManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

public class Parser {
	
	@SuppressWarnings("deprecation")
	public static ItemStack parseItem(String string){
		String item = string;
		Integer amount = Integer.parseInt(item.substring(0, item.indexOf(' ')));
		String type = item.substring(item.indexOf(' ')+1);
		if(type == null){
			type = item;
			amount = 1;
		}
		type = type.toUpperCase();
		type = type.replaceAll(" ", "_");
		Material material = Material.getMaterial(type);
		ItemStack itemstack = null;
		if(material == null){
			if(type.equalsIgnoreCase("Enchanted_Golden_Apple")) itemstack = new ItemStack(322, 1);
			else {
				FunnyGuilds.parser("Unknown item: " + string);
				return new ItemStack(Material.AIR);
			}
		} else itemstack = new ItemStack(material);
		itemstack.setAmount(amount);
		return itemstack;
	}
	
	public static Material parseMaterial(String string){
		if(string == null){
			FunnyGuilds.parser("Unknown material: null");
			return Material.AIR;
		}
		String m = string;
		m = m.toUpperCase();
		m = m.replaceAll(" ", "_");
		Material material = Material.getMaterial(m);
		if(material == null){
			if(!string.equalsIgnoreCase("ender crystal"))
				FunnyGuilds.parser("Unknown material: " + string);
			return Material.AIR;
		}
		return material;
	}
	
	public static Location parseLocation(String string){
		if(string == null) return null;
		String[] shs = string.split(",");
		if(shs.length < 4) return null;
		World world = Bukkit.getWorld(shs[0]);
		if(world == null) world = Bukkit.getWorlds().get(0);
		Location loc = new Location(world, Integer.valueOf(shs[1]), Integer.valueOf(shs[2]), Integer.valueOf(shs[3]));
		return loc;
	}
	
	public static String parseRank(String string){
		if(!string.contains("TOP-")) return null;
		StringBuilder sb = new StringBuilder();
		boolean open = false;
		boolean start = false;
		for(char c : string.toCharArray()){
			boolean end = false;
			switch(c){
				case '{': open = true; break;
				case '-': start = true; break;
				case '}': end = true; break;
				default: if(open && start) sb.append(c);
			}
			if(end) break;
		}
		try {
			int i = Integer.valueOf(sb.toString());
			if(string.contains("GTOP")){
				Guild guild = RankManager.getInstance().getGuild(i);
				if(guild != null) return StringUtils
					.replace(string, "{GTOP-" + Integer.toString(i) + '}',
							guild.getTag() + " (" + Integer.toString(guild.getRank().getPoints()) + ")");
				else return StringUtils
					.replace(string, "{GTOP-" + Integer.toString(i) + '}', "Brak");
			}else if(string.contains("PTOP")){
				User user = RankManager.getInstance().getUser(i);
				if(user != null) return StringUtils
					.replace(string, "{PTOP-" + Integer.toString(i) + '}', user.getName());
				else return StringUtils
					.replace(string, "{PTOP-" + Integer.toString(i) + '}', "Brak");
			}
		} catch (NumberFormatException e){
			FunnyGuilds.parser("Unknown number: " + sb.toString());
		}
		return null;
	}
	
	public static long parseTime(String string){
		if(string == null || string.isEmpty()) return 0;
		
		Stack<Character> type = new Stack<>();
		StringBuilder value = new StringBuilder();
		
		boolean calc = false;
		long time = 0;
		
		for(char c : string.toCharArray()){
			switch(c){
			case 'd':
			case 'h':
			case 'm':
			case 's':
				if(!calc){
					type.push(c);
					calc = true;
				}
				if(calc){
					try {
						int i = Integer.valueOf(value.toString());
						switch(type.pop()){
						case 'd': time += i*86400000; break;
						case 'h': time += i*3600000; break;
						case 'm': time += i*60000; break;
						case 's': time += i*1000; break;
						}
					} catch (NumberFormatException e){
						FunnyGuilds.parser("Unknown number: " + value.toString());
						return time;
					}
				}
				type.push(c);
				calc = true;
				break;
			default:
				value.append(c);
				break;
			}
		}
		return time;
	}
	
	public static String toString(Location loc){
		StringBuilder sb = new StringBuilder();
		sb.append(loc.getWorld().getName());
		sb.append(",");
		sb.append(loc.getBlockX());
		sb.append(",");
		sb.append(loc.getBlockY());
		sb.append(",");
		sb.append(loc.getBlockZ());
		return sb.toString();
	}
}

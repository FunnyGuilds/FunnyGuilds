package net.dzikoysk.funnyguilds.system.event.christmas;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ChristmasGift {

	private static ItemStack redGift;
	private static ItemStack greenGift;
	private static ItemStack emeraldGift;
	
	static {
		redGift = new ItemStack(Material.NOTE_BLOCK);
		ItemMeta ir = redGift.getItemMeta();
		ir.setDisplayName(ChatColor.RED + "      Czerwony Prezent");
		String[] redLore = new String[] { 
			ChatColor.YELLOW + "•" + ChatColor.RED + " 25% " + ChatColor.YELLOW + "Czerwony Prezent",
			ChatColor.YELLOW + "•" + ChatColor.RED + " 20% " + ChatColor.YELLOW + "Zielony Prezent",
			ChatColor.YELLOW + "•" + ChatColor.RED + " 13% " + ChatColor.YELLOW + "Zloto",
			ChatColor.YELLOW + "•" + ChatColor.RED + " 8%   " + ChatColor.YELLOW + "TNT",
			ChatColor.YELLOW + "•" + ChatColor.RED + " 5%   " + ChatColor.YELLOW + "Diament",
			ChatColor.YELLOW + "•" + ChatColor.RED + " 1%   " + ChatColor.YELLOW + "Emeraldowy Prezent"
		};
		ir.setLore(Arrays.asList(redLore));
		redGift.setItemMeta(ir);
		redGift.addUnsafeEnchantment(new ChristmasEnchantment(24), 24);
		
		greenGift = new ItemStack(Material.NOTE_BLOCK);
		ItemMeta ig = greenGift.getItemMeta();
		ig.setDisplayName(ChatColor.GREEN + "        Zielony Prezent");
		String[] greenLore = new String[] { 
			ChatColor.RED + "•" + ChatColor.GREEN + " 25% " + ChatColor.RED + "Zielony Prezent",
			ChatColor.RED + "•" + ChatColor.GREEN + " 20% " + ChatColor.RED + "Czerwony Prezent",
			ChatColor.RED + "•" + ChatColor.GREEN + " 16% " + ChatColor.RED + "Zelazo",
			ChatColor.RED + "•" + ChatColor.GREEN + " 8%   " + ChatColor.RED + "Zlote Jablko",
			ChatColor.RED + "•" + ChatColor.GREEN + " 4%   " + ChatColor.RED + "Emerald",
			ChatColor.RED + "•" + ChatColor.GREEN + " 1%   " + ChatColor.RED + "Emeraldowy Prezent"
		};
		ig.setLore(Arrays.asList(greenLore));
		greenGift.setItemMeta(ig);
		greenGift.addUnsafeEnchantment(new ChristmasEnchantment(24), 24);
		
		emeraldGift = new ItemStack(Material.NOTE_BLOCK);
		ItemMeta ie = emeraldGift.getItemMeta();
		ie.setDisplayName(ChatColor.AQUA + "    Emeraldowy Prezent");
		String[] emeraldLore = new String[] {
			ChatColor.AQUA + "•" + ChatColor.RED + " 80% " + ChatColor.AQUA + "Ciastko",
			ChatColor.AQUA + "•" + ChatColor.RED + " 10% " + ChatColor.AQUA + "Emeraldy",
			ChatColor.AQUA + "•" + ChatColor.RED + " 10% " + ChatColor.AQUA + "Diamenty",
			ChatColor.AQUA + "•" + ChatColor.RED + " 8%   " + ChatColor.AQUA + "Enchantowane Zlote Jablko",
			ChatColor.AQUA + "•" + ChatColor.RED + " 5%   " + ChatColor.AQUA + "Darmowa gildia",
			ChatColor.AQUA + "•" + ChatColor.RED + " 1%   " + ChatColor.AQUA + "Zycie dla gildii"
		};
		ie.setLore(Arrays.asList(emeraldLore));
		emeraldGift.setItemMeta(ie);
		emeraldGift.addUnsafeEnchantment(new ChristmasEnchantment(24), 24);
				
	}
	
	public static ItemStack getRedGift(){
		return redGift;
	}
	
	public static ItemStack getGreenGift(){
		return greenGift;
	}
	
	public static ItemStack getEmeraldGift(){
		return emeraldGift;
	}
}

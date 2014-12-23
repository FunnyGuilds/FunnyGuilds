package net.dzikoysk.funnyguilds.system.event.christmas;

import java.util.Random;

import org.bukkit.ChatColor;

public class ChristmasUtils {

	public static String color(String s, ChatColor c1, ChatColor c2, ChatColor... c3){
		StringBuilder sb = new StringBuilder();
		boolean b = false;
		for(char c : s.toCharArray()){
			if(b) sb.append(c1);
			else sb.append(c2);
			if(c3.length != 0) sb.append(c3[0]);
			sb.append(c);
			b = !b;
		}
		return sb.toString();
	}
	
	public static boolean chance(double chance) {
		return chance >= 100 || chance >= new Random().nextDouble() * 100;
	}
}

package net.dzikoysk.funnyguilds.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;

public class StringUtils {
	
	public static String replace(String text, String searchString, String replacement) {
	    if (text == null || text.isEmpty() || searchString.isEmpty()) return text;
	    if(replacement == null) replacement = "";

	    int start = 0;
	    int max = -1;
	    int end = text.indexOf(searchString, start);
	    if (end == -1) return text;

	    int replLength = searchString.length();
	    int increase = replacement.length() - replLength;
	    increase = (increase < 0 ? 0 : increase);
	    increase *= (max < 0 ? 16 : (max > 64 ? 64 : max));
	    StringBuilder sb = new StringBuilder(text.length() + increase);
	    while (end != -1) {
	        sb.append(text.substring(start, end)).append(replacement);
	        start = end + replLength;
	        if (--max == 0) break;
	        end = text.indexOf(searchString, start);
	    }
	    sb.append(text.substring(start));
	    return sb.toString();
	}
	
	public static String colored(String s){
		if(s == null) return null;
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	
	public static String toString(List<String> list, boolean send){
		StringBuilder sb = new StringBuilder();
		for(String s : list){
			sb.append(s);
			sb.append(',');
			if(send) sb.append(' ');
		}
		String s = sb.toString();
		if(send) if(s.length() > 2) s = s.substring(0, s.length()-2);
		else if(s.length() > 1) s = s.substring(0, s.length()-1);
		return s;
	}
	
	public static List<String> fromString(String s){
		List<String> list = new ArrayList<>();
		if(s == null || s.isEmpty()) return list;
		list = Arrays.asList(s.split(","));
		return list;
	}
	
	public static List<String> getEmptyList(){
		return new ArrayList<String>();
	}
}

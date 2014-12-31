package net.dzikoysk.funnyguilds.system.event.new_year;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;

public class NewYearUtils {
	
	public static Location patchLocation(Location loc){
		for(int i = 256; i > 0; i--){
			loc.setY(i);
			Material m = loc.getBlock().getType();
			if(m != null && m != Material.AIR && m != Material.BEDROCK) return loc;
		}
		return loc;
	}

	public static Color getColor(int i) {
		if(i==1) return Color.AQUA;
		if(i==2) return Color.BLACK;
		if(i==3) return Color.BLUE;
		if(i==4) return Color.FUCHSIA;
		if(i==5) return Color.GRAY;
		if(i==6) return Color.GREEN;
		if(i==7) return Color.LIME;
		if(i==8) return Color.MAROON;
		if(i==9) return Color.NAVY;
		if(i==10) return Color.OLIVE;
		if(i==11) return Color.ORANGE;
		if(i==12) return Color.PURPLE;
		if(i==13) return Color.RED;
		if(i==14) return Color.SILVER;
		if(i==15) return Color.TEAL;
		if(i==16) return Color.WHITE;
		if(i==17) return Color.YELLOW;
		return Color.WHITE;
	}
}

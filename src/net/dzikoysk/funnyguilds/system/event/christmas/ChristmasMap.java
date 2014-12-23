package net.dzikoysk.funnyguilds.system.event.christmas;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.dzikoysk.funnyguilds.FunnyGuilds;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class ChristmasMap extends MapRenderer {

	private static final int RED_GIFT = 1;
	private static final int GREEN_GIFT = 2;
	
	private final String dir;
	private boolean enabling;
	
	public ChristmasMap(int i){
		switch(i){
			case RED_GIFT: 
				this.dir = "event/christmas/red_gift_crafting.png";
				break;
			case GREEN_GIFT: 
				this.dir = "event/christmas/green_gift_crafting.png";
				break;
			default:
				this.dir = null;
		}
	}
	
	@Override
	public void render(final MapView view, final MapCanvas canvas, final Player player) {
		if(enabling || canvas == null || this.dir == null) return;
		Bukkit.getScheduler().runTaskAsynchronously(FunnyGuilds.getInstance(), new Runnable(){
			@Override
			public void run(){
				try {
					BufferedImage is = ImageIO.read(FunnyGuilds.getInstance().getResource(dir));
					if(is != null){
						enabling = true;
						canvas.drawImage(0, 0, is);
						player.sendMap(view);
					} else FunnyGuilds.error("[ChristmasMap] Dir: " + dir + " is null!");
				} catch (IOException e){
					if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
				}
			}
		});
	}

}

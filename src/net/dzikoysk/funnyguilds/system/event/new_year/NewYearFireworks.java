package net.dzikoysk.funnyguilds.system.event.new_year;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitTask;

public class NewYearFireworks {
	
	private List<BukkitTask> tasks = new ArrayList<>();
	
	public void spawn(){
		tasks.add(Bukkit.getScheduler().runTaskTimer(FunnyGuilds.getInstance(), new Runnable(){
			private Location loc = NewYearUtils.patchLocation(Bukkit.getWorlds().get(0).getSpawnLocation());
			@Override
			public void run(){
				Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
				fwm(fw);
			}
		}, 0, 10));
		
		for(Guild guild : GuildUtils.getGuilds()){
			Region region = RegionUtils.get(guild.getRegion());
			if(region == null || region.getCenter() == null) continue;
			final Location center = NewYearUtils.patchLocation(region.getCenter());
			
			tasks.add(Bukkit.getScheduler().runTaskTimer(FunnyGuilds.getInstance(), new Runnable(){
				private Location loc = center;
				@Override
				public void run(){
					Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
					fwm(fw);
				}
			}, 0, 15));
		}
		Bukkit.getScheduler().runTaskLater(FunnyGuilds.getInstance(), new Runnable(){
			@Override
			public void run(){
				for(BukkitTask task : tasks) task.cancel();
			}
		}, 1200L);
	}
	
	private void fwm(Firework fw){
		FireworkMeta fwm = fw.getFireworkMeta();
		Random r = new Random();  
        int rt = r.nextInt(4) + 1;
        Type type = Type.BALL;      
        if (rt == 1) type = Type.BALL;
        if (rt == 2) type = Type.BALL_LARGE;
        if (rt == 3) type = Type.BURST;
        if (rt == 4) type = Type.CREEPER;
        if (rt == 5) type = Type.STAR;
       
        int r1i = r.nextInt(17) + 1;
        int r2i = r.nextInt(17) + 1;
        Color c1 = NewYearUtils.getColor(r1i);
        Color c2 = NewYearUtils.getColor(r2i);
       
        FireworkEffect effect = FireworkEffect.builder()
        	.flicker(true)
        	.withColor(c1)
        	.withFade(c2)
        	.with(type)
        	.trail(true)
        	.build();
        
        fwm.addEffect(effect);
        fwm.setPower(4);
        fw.setFireworkMeta(fwm);
	}

}

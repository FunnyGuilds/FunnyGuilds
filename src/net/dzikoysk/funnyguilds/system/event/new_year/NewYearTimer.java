package net.dzikoysk.funnyguilds.system.event.new_year;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.util.StringUtils;

import org.bukkit.Bukkit;

public class NewYearTimer implements Runnable {

	private boolean newYear = false;
	
	public void start(){
		Bukkit.getScheduler().runTaskTimerAsynchronously(FunnyGuilds.getInstance(), this, 0, 20);
	}
	
	@Override
	public void run() {
		if(newYear) return;
		DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
		Date date = new Date();
		String s = dateFormat.format(date);
		if(!s.equalsIgnoreCase("2015.01.01 00:00")) return;
		newYear = true;
		Bukkit.broadcastMessage(StringUtils.colored("&6&lZyczymy Wam szczesliwego Nowego Roku!"));
		Bukkit.getScheduler().runTask(FunnyGuilds.getInstance(), new Runnable(){
			@Override
			public void run(){
				new NewYearFireworks().spawn();
			}
		});
	}

}

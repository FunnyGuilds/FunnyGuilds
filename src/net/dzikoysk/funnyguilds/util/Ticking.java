package net.dzikoysk.funnyguilds.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import net.dzikoysk.funnyguilds.FunnyGuilds;

import org.bukkit.Bukkit;

public class Ticking implements Runnable {
	
	private static Ticking instance;
	private int id;
	private long mills = System.currentTimeMillis();
	private int time;
	private static double TPS = 20.0D;

	public Ticking(){
		instance = this;
	}
	
	public void start(){
		this.mills = System.currentTimeMillis();
		this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(FunnyGuilds.getInstance(), this, 1L, 1L);
	}

	@Override
	public void run(){
		this.time += 1;
		if (this.time != 200) return;
		
		long outmils = System.currentTimeMillis();
		double r = ((outmils - this.mills) / 50.0D - 200.0D) / 10.0D;
		double tick = 20.0D - r;
		
		BigDecimal bd = new BigDecimal(tick).setScale(1, RoundingMode.UP);
		tick = Double.valueOf(bd.doubleValue()).doubleValue();
		if (tick >= 21.0D) tick = 20.0D - (tick - 20.0D);
		else if (tick >= 19.899999999999999D) tick = 20.0D;
	
		TPS = tick;
		this.time = 0;
		this.mills = System.currentTimeMillis();
	}

	public void stop(){
		if(this.id == 0) return;
		Bukkit.getScheduler().cancelTask(this.id);
	}

	public static double getTPS(){
		return TPS;
	}
	
	public static Ticking getInstance(){
		return instance;
	}
}

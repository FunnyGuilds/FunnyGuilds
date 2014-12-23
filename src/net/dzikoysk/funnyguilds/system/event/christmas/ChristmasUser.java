package net.dzikoysk.funnyguilds.system.event.christmas;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.scheduler.BukkitTask;

import net.dzikoysk.funnyguilds.basic.User;

public class ChristmasUser implements Comparable<ChristmasUser> {
	
	private static final List<ChristmasUser> USERS = new ArrayList<>();
	
	private final User user;
	private BukkitTask task;
	private int gifts;
	private int redGifts;
	private int greenGifts;
	private int emeraldGifts;
	private boolean bypass;
	private boolean seeMap;
	
	private ChristmasUser(User user){
		this.user = user;
		if(!USERS.contains(this)) USERS.add(this);
	}
	
	public static ChristmasUser get(User user){
		for(ChristmasUser cu : USERS) if(cu.getUser().equals(user)) return cu;
		return new ChristmasUser(user);
	}
	
	public void addGift(){
		this.gifts += 1;
	}
	
	public void addRedGift(){
		this.redGifts += 1;
	}
	
	public void addGreenGift(){
		this.greenGifts += 1;
	}
	
	public void addEmeraldGift(){
		this.emeraldGifts += 1;
	}
	
	public void setGifts(int i){
		this.gifts = i;
	}
	
	public void setRedGifts(int i){
		this.redGifts = i;
	}
	
	public void setGreenGifts(int i){
		this.greenGifts = i;
	}
	
	public void setEmeraldGifts(int i){
		this.emeraldGifts = i;
	}
	
	public void setBypass(boolean b){
		this.bypass = b;
	}
	
	public void setSeeMap(boolean b){
		this.seeMap = b;
	}
	
	public void setTask(BukkitTask task){
		this.task = task;
	}
	
	public int getGifts(){
		return this.gifts;
	}

	public int getRedGifts(){
		return this.redGifts;
	}
	
	public int getGreenGifts(){
		return this.greenGifts;
	}
	
	public int getEmeraldGifts(){
		return this.emeraldGifts;
	}
	
	public BukkitTask getTask(){
		return this.task;
	}
	
	public String getName(){
		return this.user.getName();
	}
	
	public User getUser(){
		return this.user;
	}
	
	public boolean hasBypass(){
		return this.bypass;
	}
	
	public boolean seeMap(){
		return this.seeMap;
	}
	
	@Override
	public boolean equals(Object o){
		if(o == null) return false;
		if(o.getClass() != this.getClass()) return false;
		ChristmasUser cu = (ChristmasUser) o;
		if(!this.getUser().equals(cu.getUser())) return false;
		return true;
	}

	@Override
	public int compareTo(ChristmasUser cu) {
		int i = Integer.compare(gifts, cu.getGifts());
		if(i == 0) i = user.getName().compareTo(cu.getUser().getName());
		return i;
	}
	
	public static List<ChristmasUser> getUsers(){
		return USERS;
	}
}

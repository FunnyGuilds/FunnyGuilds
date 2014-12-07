package net.dzikoysk.funnyguilds.basic;

import net.dzikoysk.funnyguilds.basic.util.RegionUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class Region {
		
	private String name;
	private Guild guild;
	private Location center;
	private World world;
	private int size;
	private int enlarge;
	private Location l;
	private Location p;
	private boolean changes;
	
	private Region(String name){
		this.name = name;
		this.changes = true;
		RegionUtils.addRegion(this);
	}
	
	public Region(Guild guild, Location loc, int size){
		this.guild = guild;
		this.name = guild.getName();
		this.world = loc.getWorld();
		this.center = loc;
		this.size = size;
		this.update();
		RegionUtils.addRegion(this);
		this.changes = true;
	}
	
	public static Region get(String name){
		for(Region region : RegionUtils.getRegions())
			if(region.getName() != null && region.getName().equalsIgnoreCase(name)) return region;
		return new Region(name);
	}
	
	public void update(){
		if(this.center == null) return;
		if(this.size < 1) return;
		if(this.world == null) this.world = Bukkit.getWorlds().get(0);
		if(this.world != null){
			int lx = this.center.getBlockX() + this.size;
			int lz = this.center.getBlockZ() + this.size;
			
			int px = this.center.getBlockX() - this.size;
			int pz = this.center.getBlockZ() - this.size;
			
			Vector l = new Vector(lx, 0, lz);
			Vector p = new Vector(px, this.world.getMaxHeight(), pz);
			
			this.l = l.toLocation(this.world);
			this.p = p.toLocation(this.world);
		}
		this.changes();
	}
	
	public void setName(String s){
		this.name = s;
		this.changes();
	}
	
	public void setGuild(Guild guild){
		this.guild = guild;
		this.changes();
	}
	
	public void setCenter(Location loc){
		this.center = loc;
		this.world = loc.getWorld();
		this.update();
		this.changes();
	}
	
	public void setSize(int i){
		this.size = i;
		this.update();
		this.changes();
	}
	
	public void setWorld(World world){
		this.world = world;
		this.update();
		this.changes();
	}
	
	public void setL(Location loc){
		this.l = loc;
		this.changes();
	}
	
	public void setP(Location loc){
		this.p = loc;
		this.changes();
	}
	
	public void setEnlarge(int i){
		this.enlarge = i;
		this.changes();
	}
	
	public void delete(){
		RegionUtils.removeRegion(this);
		this.guild = null;
		this.world = null;
		this.center = null;
		this.l = null;
		this.p = null;
	}
	
	public boolean isIn(Location loc){
		this.update();
		if(loc == null || this.l == null || this.p == null) return false;
		if(!this.center.getWorld().equals(loc.getWorld())) return false;
		if(loc.getBlockX() > this.getLowerX() && loc.getBlockX() < this.getUpperX()) {
            if(loc.getBlockY() > this.getLowerY() && loc.getBlockY() < this.getUpperY()) {
                if(loc.getBlockZ() > this.getLowerZ() && loc.getBlockZ() < this.getUpperZ()){
                    return true;
                }
            }
        }
		return false;
	}
	
	public String getName(){
		return this.name;
	}
	
	public Guild getGuild(){
		return this.guild;
	}

	public Location getCenter(){
		return this.center;
	}
	
	public int getSize(){
		return this.size;
	}
	
	public World getWorld(){
		return this.world;
	}
	
	public Location getL(){
		return this.l;
	}
	
	public Location getP(){
		return this.p;
	}
	
	public int getEnlarge(){
		return this.enlarge;
	}
	
	public int getUpperX(){
		int x = this.l.getBlockX();
		int y = this.p.getBlockX();
		if(y < x) return x;
		return y;
	}
	
	public int getUpperY(){
		int x = this.l.getBlockY();
		int y = this.p.getBlockY();
		if(y < x) return x;
		return y;
	}
	
	public int getUpperZ(){
		int x = this.l.getBlockZ();
		int y = this.p.getBlockZ();
		if(y < x) return x;
		return y;
	}
	
	public int getLowerX(){
		int x = this.l.getBlockX();
		int y = this.p.getBlockX();
		if(x > y) return y;
		return x;
	}
	
	public int getLowerY(){
		int x = this.l.getBlockY();
		int y = this.p.getBlockY();
		if(x > y) return y;
		return x;
	}
	
	public int getLowerZ(){
		int x = this.l.getBlockZ();
		int y = this.p.getBlockZ();
		if(x > y) return y;
		return x;
	}
	
	private void changes(){
		this.changes = true;
	}
	
	public boolean changed(){
		boolean c = changes;
		this.changes = false;
		return c;
	}
	
	@Override
	public String toString(){
		return this.name;
	}
}
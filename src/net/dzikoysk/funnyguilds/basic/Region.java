package net.dzikoysk.funnyguilds.basic;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

public class Region {
		
	private final String name;
	
	private Guild guild;

	private Location center;
	private World world;
	private int size;
	
	private Location l;
	private Location p;
	
	private int enlarge;
	
	private Region(String name){
		this.name = name;
		RegionUtils.addRegion(this);
	}
	
	public Region(Guild guild, Location loc, int size){
		this.guild = guild;
		this.name = guild.getName();
		this.world = loc.getWorld();
		this.center = loc;
		this.size = size;
		RegionUtils.addRegion(this);
		this.update();
	}
	
	public static Region get(String name){
		for(Region region : RegionUtils.getRegions())
			if(region.getName() != null && region.getName().equalsIgnoreCase(name)) return region;
		return new Region(name);
	}
	
	public YamlConfiguration serialize(YamlConfiguration yaml) {
		yaml.set("name", this.name);
		yaml.set("center", this.world.getName() + "," + this.center.getBlockX() + "," + this.center.getBlockY() + "," + this.center.getBlockZ());
		yaml.set("size", this.size);
		yaml.set("enlarge", this.enlarge);
		return yaml;
	}
	
	public static Region deserialize(Object[] values){
		if(values == null){
			FunnyGuilds.error("Cannot deserialize region! Caused by: null");
			return null;
		}
		Region region = Region.get((String) values[0]);
		region.setCenter((Location) values[1]);
		region.setSize((int) values[2]);
		region.setEnlarge((int) values[3]);
		region.update();
		return region;
	}
	
	public void update(){
		if(this.center != null){
			if(this.size > 0){
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
			}
		}
	}
	
	public void setGuild(Guild guild){
		this.guild = guild;
	}
	
	public void setCenter(Location loc){
		this.center = loc;
		this.world = loc.getWorld();
		this.update();
	}
	
	public void setSize(int i){
		this.size = i;
		this.update();
	}
	
	public void setWorld(World world){
		this.world = world;
		this.update();
	}
	
	public void setL(Location loc){
		this.l = loc;
	}
	
	public void setP(Location loc){
		this.p = loc;
	}
	
	public void setEnlarge(int i){
		this.enlarge = i;
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
}
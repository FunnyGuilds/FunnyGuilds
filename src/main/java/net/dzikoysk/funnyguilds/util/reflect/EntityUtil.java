package net.dzikoysk.funnyguilds.util.reflect;

import java.util.HashMap;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class EntityUtil {
	
	private static Class<?> entityClass = Reflections.getCraftClass("Entity");
	private static Class<?> enderCrystalClass = Reflections.getCraftClass("EntityEnderCrystal");
	private static Class<?> spawnEntityClass = Reflections.getCraftClass("PacketPlayOutSpawnEntity");
    private static Class<?> despawnEntityClass = Reflections.getCraftClass("PacketPlayOutEntityDestroy");
    
    public static HashMap<Guild, Integer> map = new HashMap<>();
	private static HashMap<Integer, Object> ids = new HashMap<>();
	
	private static int spawnPacket(Location loc) throws Exception {
		Object world = Reflections.getHandle(loc.getWorld());	
		Object crystal = enderCrystalClass.getConstructor(Reflections.getCraftClass("World")).newInstance(world);
		Reflections.getMethod(enderCrystalClass, "setLocation", double.class, double.class, double.class, float.class, float.class).invoke(crystal, loc.getX(), loc.getY(), loc.getZ(), 0, 0);
		Object packet = spawnEntityClass.getConstructor(new Class<?>[]{ entityClass, int.class }).newInstance(crystal, 51);
		int id = (int) Reflections.getMethod(enderCrystalClass, "getId").invoke(crystal);
		ids.put(id, packet);
		return id;
	}
	
	private static Object despawnPacket(int id) throws Exception {
		Object packet = despawnEntityClass.getConstructor(new Class<?>[]{ int[].class }).newInstance(new int[] { id });
		return packet;
	}
	
	public static void spawn(Guild guild) {
		try {
			Object o = null;
			if(!map.containsKey(guild)){
				Location loc = Region.get(guild.getRegion()).getCenter();
				if(loc == null) return;
				int id = spawnPacket(loc);
				o = ids.get(id);
				map.put(guild, id);
			} else o = ids.get(map.get(guild));
			PacketSender.sendPacket(Bukkit.getOnlinePlayers(), o);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static void spawn(Player... players){
		for(Guild guild : GuildUtils.getGuilds())
			try {
				Object o = null;
				if(!map.containsKey(guild)){
					Location loc = Region.get(guild.getRegion()).getCenter();
					if(loc == null) continue;
					int id = spawnPacket(loc);
					o = ids.get(id);
					map.put(guild, id);
				} else o = ids.get(map.get(guild));
				PacketSender.sendPacket(players, o);
			} catch (Exception e){
				e.printStackTrace();
			}
	}
	
	public static void despawn(Guild guild) {
		try {
			int id = map.get(guild);
			ids.remove(id);
			map.remove(guild);
			Object o = despawnPacket(id);
			PacketSender.sendPacket(Bukkit.getOnlinePlayers(), o);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static void despawn(Player... players){
		for(Guild guild : GuildUtils.getGuilds())
			try {
				int id = map.get(guild);
				ids.remove(id);
				map.remove(guild);
				Object o = despawnPacket(id);
				PacketSender.sendPacket(players, o);
			} catch (Exception e){
				e.printStackTrace();
			}
	}
	
	public static void despawn() {
		for(Guild guild : GuildUtils.getGuilds())
			try {
				int id = map.get(guild);
				ids.remove(id);
				map.remove(guild);
				Object o = despawnPacket(id);
				PacketSender.sendPacket(Bukkit.getOnlinePlayers(), o);
			} catch (Exception e){
				e.printStackTrace();
			}
	}
}

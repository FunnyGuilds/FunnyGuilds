package net.dzikoysk.funnyguilds.util;

import java.lang.reflect.Method;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.OfflineUser;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PacketUtils {
	
	private static final String packageName = Bukkit.getServer().getClass().getPackage().getName();
    private static final String version = packageName.substring(packageName.lastIndexOf(".") + 1);
    
    private static final Class<?> packetClass;
	private static final Class<?>[] typesClass;
	private static int type = 0;
	
    static{
		packetClass = ReflectionUtils.getCraftClass("PacketPlayOutPlayerInfo");
		typesClass = new Class<?>[] { String.class, boolean.class, int.class };
		
		try {
			if(packetClass.getConstructor(typesClass) == null) type = 1;
		} catch (Exception e) {
			type = 1;
		}
	}
    
	public static void sendPacket(Player p, Object... os){
		try {
	    	Class<?> packetClass = Class.forName("net.minecraft.server." + version + ".Packet");
	    	Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");

	      	Object cp = craftPlayer.cast(p);
	       	Object handle = craftPlayer.getMethod("getHandle").invoke(cp);
	      	Object con = handle.getClass().getField("playerConnection").get(handle);
	       	Method method = con.getClass().getMethod("sendPacket", packetClass);
	        for(Object o : os){
	        	if(o == null) continue;
	        	method.invoke(con, o);
	        }
	   	} catch (Exception e){
	        if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
	    }
	}
	
	public static Object getPacket(String s, boolean b, int i){
		if(type == 0){
			try {
				return packetClass.getConstructor(typesClass).newInstance(s, b, i);
			} catch (Exception e){
				if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
			}
		}else{
			try {
				Class<?> clazz = ReflectionUtils.getCraftClass("PacketPlayOutPlayerInfo");
				Object packet = packetClass.getConstructor().newInstance();
				ReflectionUtils.getPrivateField(clazz, "username").set(packet, s);
				ReflectionUtils.getPrivateField(clazz, "gamemode").set(packet, 1);
				ReflectionUtils.getPrivateField(clazz, "ping").set(packet, i);
				ReflectionUtils.getPrivateField(clazz, "player").set(packet, new OfflineUser(s).getProfile());
				if(!b) ReflectionUtils.getPrivateField(clazz, "action").set(packet, 4);
				return packet;
			} catch (Exception e){
				if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
			}
		}
		return null;
	}
}
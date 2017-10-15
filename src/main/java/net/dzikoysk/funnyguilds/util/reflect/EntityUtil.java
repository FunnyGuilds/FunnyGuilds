package net.dzikoysk.funnyguilds.util.reflect;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class EntityUtil {

    private static Constructor<?> spawnEntityConstructor;
    private static Constructor<?> despawnEntityConstructor;
    private static Constructor<?> enderCrystalConstructor;

    private static Method setLocation;
    private static Method getId;

    private static final Map<Guild, Integer> entitesMap = new HashMap<>();
    private static final Map<Integer, Object> ids = new HashMap<>();

    static {
        final Class<?> entityClass = Reflections.getCraftClass("Entity");
        final Class<?> enderCrystalClass = Reflections.getCraftClass("EntityEnderCrystal");
        final Class<?> spawnEntityClass = Reflections.getCraftClass("PacketPlayOutSpawnEntity");
        final Class<?> despawnEntityClass = Reflections.getCraftClass("PacketPlayOutEntityDestroy");
        final Class<?> craftWorldClass = Reflections.getCraftClass("World");

        spawnEntityConstructor = Reflections.getConstructor(spawnEntityClass, entityClass, int.class);
        despawnEntityConstructor = Reflections.getConstructor(despawnEntityClass, int[].class);
        enderCrystalConstructor = Reflections.getConstructor(enderCrystalClass, craftWorldClass);

        setLocation = Reflections.getMethod(enderCrystalClass, "setLocation", double.class, double.class, double.class, float.class, float.class);
        getId = Reflections.getMethod(enderCrystalClass, "getId");
    }

    public static Map<Guild, Integer> getEntitesMap() {
        return entitesMap;
    }

    private static int spawnPacket(Location loc) throws Exception {
        Object world = Reflections.getHandle(loc.getWorld());
        Object crystal = enderCrystalConstructor.newInstance(world);
        setLocation.invoke(crystal, loc.getX(), loc.getY(), loc.getZ(), 0, 0);
        Object packet = spawnEntityConstructor.newInstance(crystal, 51);
        int id = (int) getId.invoke(crystal);
        ids.put(id, packet);
        return id;
    }

    private static Object despawnPacket(int id) throws Exception {
        return despawnEntityConstructor.newInstance(new int[]{id});
    }

    public static void spawn(Guild guild) {
        try {
            Object o = null;
            if (!entitesMap.containsKey(guild)) {
                Location center = Region.get(guild.getRegion()).getCenter();
                if (center == null) {
                    return;
                }
                Location loc = new Location(center.getWorld(), center.getX() + 0.5D, center.getY() - 1.0D, center.getZ() + 0.5D);
                int id = spawnPacket(loc);
                o = ids.get(id);
                entitesMap.put(guild, id);
            } else {
                o = ids.get(entitesMap.get(guild));
            }
            PacketSender.sendPacket(Bukkit.getOnlinePlayers(), o);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void spawn(Guild guild, Player... players) {
        try {
            Object o = null;
            if (!entitesMap.containsKey(guild)) {
                Location center = Region.get(guild.getRegion()).getCenter();
                if (center == null) {
                    return;
                }
                Location loc = new Location(center.getWorld(), center.getX() + 0.5D, center.getY() - 1.0D, center.getZ() + 0.5D);
                int id = spawnPacket(loc);
                o = ids.get(id);
                entitesMap.put(guild, id);
            } else {
                o = ids.get(entitesMap.get(guild));
            }
            PacketSender.sendPacket(players, o);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void despawn(Guild guild) {
        try {
            int id = entitesMap.get(guild);
            ids.remove(id);
            entitesMap.remove(guild);
            Object o = despawnPacket(id);
            PacketSender.sendPacket(Bukkit.getOnlinePlayers(), o);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void despawn(Guild guild, Player... players) {
        try {
            int id = entitesMap.get(guild);
            Object o = despawnPacket(id);
            PacketSender.sendPacket(players, o);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void despawn() {
        for (Guild guild : GuildUtils.getGuilds()) {
            try {
                int id = entitesMap.get(guild);
                ids.remove(id);
                entitesMap.remove(guild);
                Object o = despawnPacket(id);
                PacketSender.sendPacket(Bukkit.getOnlinePlayers(), o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

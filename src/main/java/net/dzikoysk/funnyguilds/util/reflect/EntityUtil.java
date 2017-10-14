package net.dzikoysk.funnyguilds.util.reflect;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class EntityUtil {

    public static HashMap<Guild, Integer> entitesMap = new HashMap<>();
    private static Class<?> entityClass = Reflections.getCraftClass("Entity");
    private static Class<?> enderCrystalClass = Reflections.getCraftClass("EntityEnderCrystal");
    private static Class<?> spawnEntityClass = Reflections.getCraftClass("PacketPlayOutSpawnEntity");
    private static Class<?> despawnEntityClass = Reflections.getCraftClass("PacketPlayOutEntityDestroy");
    private static HashMap<Integer, Object> ids = new HashMap<>();

    public static HashMap<Guild, Integer> getEntitesMap() {
        return entitesMap;
    }

    private static int spawnPacket(Location loc) throws Exception {
        Object world = Reflections.getHandle(loc.getWorld());
        Object crystal = enderCrystalClass.getConstructor(Reflections.getCraftClass("World")).newInstance(world);
        Reflections.getMethod(enderCrystalClass, "setLocation", double.class, double.class, double.class, float.class, float.class).invoke(crystal, loc.getX(), loc.getY(), loc.getZ(), 0, 0);
        Object packet = spawnEntityClass.getConstructor(new Class<?>[]{entityClass, int.class}).newInstance(crystal, 51);
        int id = (int) Reflections.getMethod(enderCrystalClass, "getId").invoke(crystal);
        ids.put(id, packet);
        return id;
    }

    private static Object despawnPacket(int id) throws Exception {
        return despawnEntityClass.getConstructor(new Class<?>[]{int[].class}).newInstance(new int[]{id});
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

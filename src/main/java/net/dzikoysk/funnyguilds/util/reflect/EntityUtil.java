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

    private static final Constructor<?> SPAWN_ENTITY_CONSTRUCTOR;
    private static final Constructor<?> DESPAWN_ENTITY_CONSTRUCTOR;
    private static final Constructor<?> ENDER_CRYSTAL_CONSTRUCTOR;

    private static final Method SET_LOCATION;
    private static final Method GET_ID;

    private static final Map<Guild, Integer> ENTITY_MAP = new HashMap<>();
    private static final Map<Integer, Object> ID_MAP = new HashMap<>();

    static {
        final Class<?> entityClass = Reflections.getCraftClass("Entity");
        final Class<?> enderCrystalClass = Reflections.getCraftClass("EntityEnderCrystal");
        final Class<?> spawnEntityClass = Reflections.getCraftClass("PacketPlayOutSpawnEntity");
        final Class<?> despawnEntityClass = Reflections.getCraftClass("PacketPlayOutEntityDestroy");
        final Class<?> craftWorldClass = Reflections.getCraftClass("World");

        SPAWN_ENTITY_CONSTRUCTOR = Reflections.getConstructor(spawnEntityClass, entityClass, int.class);
        DESPAWN_ENTITY_CONSTRUCTOR = Reflections.getConstructor(despawnEntityClass, int[].class);
        ENDER_CRYSTAL_CONSTRUCTOR = Reflections.getConstructor(enderCrystalClass, craftWorldClass);

        SET_LOCATION = Reflections.getMethod(enderCrystalClass, "setLocation", double.class, double.class, double.class, float.class, float.class);
        GET_ID = Reflections.getMethod(enderCrystalClass, "getId");
    }

    public static Map<Guild, Integer> getEntitesMap() {
        return ENTITY_MAP;
    }

    private static int spawnPacket(Location loc) throws Exception {
        Object world = Reflections.getHandle(loc.getWorld());
        Object crystal = ENDER_CRYSTAL_CONSTRUCTOR.newInstance(world);
        SET_LOCATION.invoke(crystal, loc.getX(), loc.getY(), loc.getZ(), 0, 0);
        Object packet = SPAWN_ENTITY_CONSTRUCTOR.newInstance(crystal, 51);
        int id = (int) GET_ID.invoke(crystal);
        ID_MAP.put(id, packet);
        return id;
    }

    private static Object despawnPacket(int id) throws Exception {
        return DESPAWN_ENTITY_CONSTRUCTOR.newInstance(new int[]{id});
    }

    public static void spawn(Guild guild) {
        try {
            Object o = null;
            if (!ENTITY_MAP.containsKey(guild)) {
                Location center = Region.get(guild.getRegion()).getCenter();
                if (center == null) {
                    return;
                }
                Location loc = new Location(center.getWorld(), center.getX() + 0.5D, center.getY() - 1.0D, center.getZ() + 0.5D);
                int id = spawnPacket(loc);
                o = ID_MAP.get(id);
                ENTITY_MAP.put(guild, id);
            } else {
                o = ID_MAP.get(ENTITY_MAP.get(guild));
            }
            PacketSender.sendPacket(Bukkit.getOnlinePlayers(), o);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void spawn(Guild guild, Player... players) {
        try {
            Object o = null;
            if (!ENTITY_MAP.containsKey(guild)) {
                Location center = Region.get(guild.getRegion()).getCenter();
                if (center == null) {
                    return;
                }
                Location loc = new Location(center.getWorld(), center.getX() + 0.5D, center.getY() - 1.0D, center.getZ() + 0.5D);
                int id = spawnPacket(loc);
                o = ID_MAP.get(id);
                ENTITY_MAP.put(guild, id);
            } else {
                o = ID_MAP.get(ENTITY_MAP.get(guild));
            }
            PacketSender.sendPacket(players, o);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void despawn(Guild guild) {
        try {
            if (!ENTITY_MAP.containsKey(guild)) {
                return;
            }
            int id = ENTITY_MAP.get(guild);
            ID_MAP.remove(id);
            ENTITY_MAP.remove(guild);
            Object o = despawnPacket(id);
            PacketSender.sendPacket(Bukkit.getOnlinePlayers(), o);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void despawn(Guild guild, Player... players) {
        try {
            int id = ENTITY_MAP.get(guild);
            Object o = despawnPacket(id);
            PacketSender.sendPacket(players, o);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void despawn() {
        for (Guild guild : GuildUtils.getGuilds()) {
            try {
                int id = ENTITY_MAP.get(guild);
                ID_MAP.remove(id);
                ENTITY_MAP.remove(guild);
                Object o = despawnPacket(id);
                PacketSender.sendPacket(Bukkit.getOnlinePlayers(), o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

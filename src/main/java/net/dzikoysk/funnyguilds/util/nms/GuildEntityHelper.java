package net.dzikoysk.funnyguilds.util.nms;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class GuildEntityHelper {

    private static final Constructor<?> SPAWN_ENTITY_CONSTRUCTOR;
    private static final Constructor<?> SPAWN_ENTITY_LIVING_CONSTRUCTOR;
    private static final Constructor<?> DESPAWN_ENTITY_CONSTRUCTOR;
    private static final Constructor<?> ENTITY_CONSTRUCTOR;

    private static final Method SET_LOCATION;
    private static final Method GET_ID;

    private static final Map<Guild, Integer> ENTITY_MAP = new ConcurrentHashMap<>();
    private static final Map<Integer, Object> ID_MAP = new HashMap<>();
    
    private static final ObjectType OBJECT_TYPE;

    static {
        EntityType entityType = FunnyGuilds.getInstance().getPluginConfiguration().createEntityType;
        String entityTypeName = entityType == null ? "EnderCrystal" : entityType.getEntityClass().getSimpleName();
        
        final Class<?> generalEntityClass = Reflections.getNMSClass("Entity");
        final Class<?> entityLivingClass = Reflections.getNMSClass("EntityLiving");
        final Class<?> entityClass = Reflections.getNMSClass("Entity" + entityTypeName);
        final Class<?> spawnEntityClass = Reflections.getNMSClass("PacketPlayOutSpawnEntity");
        final Class<?> spawnEntityLivingClass = Reflections.getNMSClass("PacketPlayOutSpawnEntityLiving");
        final Class<?> despawnEntityClass = Reflections.getNMSClass("PacketPlayOutEntityDestroy");
        final Class<?> craftWorldClass = Reflections.getNMSClass("World");

        SPAWN_ENTITY_CONSTRUCTOR = Reflections.getConstructor(spawnEntityClass, generalEntityClass, int.class);
        SPAWN_ENTITY_LIVING_CONSTRUCTOR = Reflections.getConstructor(spawnEntityLivingClass, entityLivingClass);
        DESPAWN_ENTITY_CONSTRUCTOR = Reflections.getConstructor(despawnEntityClass, int[].class);

        switch (Reflections.SERVER_VERSION) {
            case "v1_14_R1":
            case "v1_15_R1":
            case "v1_16_R1":
            case "v1_16_R2":
            case "v1_16_R3":
                ENTITY_CONSTRUCTOR = Reflections.getConstructor(entityClass, craftWorldClass, double.class, double.class, double.class);
                break;
            default:
                ENTITY_CONSTRUCTOR = Reflections.getConstructor(entityClass, craftWorldClass);
                break;
        }

        SET_LOCATION = Reflections.getMethod(entityClass, "setLocation", double.class, double.class, double.class, float.class, float.class);
        GET_ID = Reflections.getMethod(entityClass, "getId");
        
        OBJECT_TYPE = entityLivingClass.isAssignableFrom(entityClass) ? null : ObjectType.get(entityType);
    }

    public static Map<Guild, Integer> getGuildEntities() {
        return ENTITY_MAP;
    }

    private static int createSpawnGuildHeartPacket(Location loc) throws Exception {
        Object world = Reflections.getHandle(loc.getWorld());

        Object entity;

        switch (Reflections.SERVER_VERSION) {
            case "v1_14_R1":
            case "v1_15_R1":
            case "v1_16_R1":
            case "v1_16_R2":
            case "v1_16_R3":
                entity = ENTITY_CONSTRUCTOR.newInstance(world, loc.getX(), loc.getY(), loc.getZ());
                break;
            default:
                entity = ENTITY_CONSTRUCTOR.newInstance(world);
                SET_LOCATION.invoke(entity, loc.getX(), loc.getY(), loc.getZ(), 0, 0);
                break;
        }

        //todo
        Object packet = null;
        if (OBJECT_TYPE == null) {
            packet = SPAWN_ENTITY_LIVING_CONSTRUCTOR.newInstance(entity);
        } else {
            packet = SPAWN_ENTITY_CONSTRUCTOR.newInstance(entity, OBJECT_TYPE.getObjectID());
        }
        
        int id = (int) GET_ID.invoke(entity);
        ID_MAP.put(id, packet);
        
        return id;
    }

    private static Object createDespawnGuildHeartPacket(int id) throws Exception {
        return DESPAWN_ENTITY_CONSTRUCTOR.newInstance(new int[]{id});
    }

    public static void spawnGuildHeart(Guild guild) {
        spawnGuildHeart(guild, Bukkit.getOnlinePlayers().toArray(new Player[0]));
    }

    public static void spawnGuildHeart(Guild guild, Player... players) {
        try {
            Object value;

            if (!ENTITY_MAP.containsKey(guild)) {
                Region region = guild.getRegion();

                if (region == null) {
                    return;
                }

                Location center = region.getCenter();

                if (center == null) {
                    return;
                }

                int id = createSpawnGuildHeartPacket(center.clone().add(0.5D, - 1.0D, 0.5D));
                
                value = ID_MAP.get(id);
                ENTITY_MAP.put(guild, id);
            } else {
                value = ID_MAP.get(ENTITY_MAP.get(guild));
            }
            
            PacketSender.sendPacket(players, value);
        } catch (Exception exception) {
            FunnyGuilds.getPluginLogger().error("Could not spawn guild heart", exception);
        }

    }

    public static void despawnGuildHeart(Guild guild) {
        try {
            if (!ENTITY_MAP.containsKey(guild)) {
                return;
            }
            
            int id = ENTITY_MAP.get(guild);
            
            ID_MAP.remove(id);
            ENTITY_MAP.remove(guild);

            Object o = createDespawnGuildHeartPacket(id);
            PacketSender.sendPacket(Bukkit.getOnlinePlayers(), o);
        } catch (Exception exception) {
            FunnyGuilds.getPluginLogger().error("Could not despawn guild heart", exception);
        }
    }

    public static void despawnGuildHeart(Guild guild, Player... players) {
        try {
            if (!ENTITY_MAP.containsKey(guild)) {
                return;
            }
            
            int id = ENTITY_MAP.get(guild);
            Object o = createDespawnGuildHeartPacket(id);
            
            PacketSender.sendPacket(players, o);
        } catch (Exception exception) {
            FunnyGuilds.getPluginLogger().error("Could not despawn guild heart", exception);
        }
    }

    public static void despawnGuildHearts() {
        for (Guild guild : GuildUtils.getGuilds()) {
            despawnGuildHeart(guild);
        }
    }

    private GuildEntityHelper() {
    }

}

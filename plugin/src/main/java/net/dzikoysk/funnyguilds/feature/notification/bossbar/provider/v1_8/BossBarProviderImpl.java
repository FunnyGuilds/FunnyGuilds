package net.dzikoysk.funnyguilds.feature.notification.bossbar.provider.v1_8;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.notification.NotificationUtil;
import net.dzikoysk.funnyguilds.feature.notification.bossbar.provider.BossBarOptions;
import net.dzikoysk.funnyguilds.feature.notification.bossbar.provider.BossBarProvider;
import net.dzikoysk.funnyguilds.nms.PacketSender;
import net.dzikoysk.funnyguilds.nms.Reflections;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class BossBarProviderImpl implements BossBarProvider {

    private static final Class<?> ENTITY_CLASS;
    private static final Class<?> ENTITY_LIVING_CLASS;
    private static final Class<?> ENTITY_WITHER_CLASS;
    private static final Class<?> PACKET_PLAY_OUT_SPAWN_ENTITY_LIVING_CLASS;
    private static final Class<?> PACKET_PLAY_OUT_ENTITY_DESTROY_CLASS;
    private static final Class<?> PACKET_PLAY_OUT_ENTITY_TELEPORT_CLASS;

    private static final Constructor<?> ENTITY_WITHER_CONSTRUCTOR;
    private static final Constructor<?> PACKET_PLAY_OUT_SPAWN_ENTITY_LIVING_CONSTRUCTOR;
    private static final Constructor<?> PACKET_PLAY_OUT_ENTITY_DESTROY_CONSTRUCTOR;
    private static final Constructor<?> PACKET_PLAY_OUT_ENTITY_TELEPORT_CONSTRUCTOR;

    private static final Method ENTITY_WITHER_SET_LOCATION;
    private static final Method ENTITY_WITHER_SET_INVISIBLE;
    private static final Method ENTITY_WITHER_SET_CUSTOM_NAME;
    private static final Method ENTITY_WITHER_GET_ID;

    private static final Field ENTITY_WITHER_MOT_X;
    private static final Field ENTITY_WITHER_MOT_Y;
    private static final Field ENTITY_WITHER_MOT_Z;

    static {
        ENTITY_CLASS = Reflections.getNMSClass("Entity");
        ENTITY_LIVING_CLASS = Reflections.getNMSClass("EntityLiving");
        ENTITY_WITHER_CLASS = Reflections.getNMSClass("EntityWither");
        PACKET_PLAY_OUT_SPAWN_ENTITY_LIVING_CLASS = Reflections.getNMSClass("PacketPlayOutSpawnEntityLiving");
        PACKET_PLAY_OUT_ENTITY_DESTROY_CLASS = Reflections.getNMSClass("PacketPlayOutEntityDestroy");
        PACKET_PLAY_OUT_ENTITY_TELEPORT_CLASS = Reflections.getNMSClass("PacketPlayOutEntityTeleport");

        ENTITY_WITHER_CONSTRUCTOR = Reflections.getConstructor(ENTITY_WITHER_CLASS, Reflections.getNMSClass("World"));
        PACKET_PLAY_OUT_SPAWN_ENTITY_LIVING_CONSTRUCTOR = Reflections.getConstructor(PACKET_PLAY_OUT_SPAWN_ENTITY_LIVING_CLASS, ENTITY_LIVING_CLASS);
        PACKET_PLAY_OUT_ENTITY_DESTROY_CONSTRUCTOR = Reflections.getConstructor(PACKET_PLAY_OUT_ENTITY_DESTROY_CLASS, int[].class);
        PACKET_PLAY_OUT_ENTITY_TELEPORT_CONSTRUCTOR = Reflections.getConstructor(
                PACKET_PLAY_OUT_ENTITY_TELEPORT_CLASS,
                int.class, int.class, int.class, int.class, byte.class, byte.class, boolean.class
        );

        ENTITY_WITHER_SET_LOCATION = Reflections.getMethod(
                ENTITY_WITHER_CLASS,
                "setLocation",
                double.class, double.class, double.class, float.class, float.class
        );
        ENTITY_WITHER_SET_INVISIBLE = Reflections.getMethod(
                ENTITY_WITHER_CLASS,
                "setInvisible",
                boolean.class
        );

        ENTITY_WITHER_SET_CUSTOM_NAME = Reflections.getMethod(
                ENTITY_WITHER_CLASS,
                "setCustomName",
                Reflections.USE_PRE_13_METHODS ? String.class : Reflections.getNMSClass("IChatBaseComponent")
        );

        ENTITY_WITHER_GET_ID = Reflections.getMethod(ENTITY_WITHER_CLASS, "getId");

        ENTITY_WITHER_MOT_X = Reflections.getField(ENTITY_CLASS, "motX");
        ENTITY_WITHER_MOT_Y = Reflections.getField(ENTITY_CLASS, "motY");
        ENTITY_WITHER_MOT_Z = Reflections.getField(ENTITY_CLASS, "motZ");
    }

    private final User user;
    private volatile BukkitTask bossBarHandleTask;
    private Object witherInstance;
    private final AtomicInteger currentSecond;
    private int witherId;

    public BossBarProviderImpl(User user) {
        this.user = user;
        this.currentSecond = new AtomicInteger(0);
    }

    @Override
    public void sendNotification(String text, BossBarOptions options, int timeout) {
        Player player = user.getPlayer();

        if (player == null) {
            return;
        }

        if (this.bossBarHandleTask != null) {
            this.bossBarHandleTask.cancel();
            this.removeBossBar(player);
            this.currentSecond.set(0);
        }

        this.createBossBar(player, text, timeout);
    }

    @Override
    public void removeNotification() {
        Player player = user.getPlayer();

        if (player == null) {
            return;
        }

        this.removeBossBar(player);
    }

    private void createBossBar(Player player, String text, int timeout) {
        try {
            this.witherInstance = ENTITY_WITHER_CONSTRUCTOR.newInstance(Reflections.getHandle(player.getWorld()));

            Location witherLocation = player.getLocation().add(player.getLocation().getDirection().normalize().multiply(30));

            ENTITY_WITHER_SET_LOCATION.invoke(
                    this.witherInstance,
                    witherLocation.getX(), witherLocation.getY(), witherLocation.getZ(), witherLocation.getYaw(), witherLocation.getPitch()
            );
            ENTITY_WITHER_SET_INVISIBLE.invoke(this.witherInstance, true);
            ENTITY_WITHER_SET_CUSTOM_NAME.invoke(
                    this.witherInstance, Reflections.USE_PRE_13_METHODS ? text : NotificationUtil.createBaseComponent(text, false));

            ENTITY_WITHER_MOT_X.set(this.witherInstance, 0);
            ENTITY_WITHER_MOT_Y.set(this.witherInstance, 0);
            ENTITY_WITHER_MOT_Z.set(this.witherInstance, 0);

            this.witherId = (int) ENTITY_WITHER_GET_ID.invoke(witherInstance);

            Object spawnPacket = PACKET_PLAY_OUT_SPAWN_ENTITY_LIVING_CONSTRUCTOR.newInstance(this.witherInstance);
            PacketSender.sendPacket(player, spawnPacket);

            this.bossBarHandleTask = Bukkit.getScheduler().runTaskTimerAsynchronously(FunnyGuilds.getInstance(), () -> {

                if (this.currentSecond.getAndIncrement() >= timeout * 2) {
                    this.bossBarHandleTask.cancel();
                    this.removeBossBar(player);
                    return;
                }

                this.updateBossBar(player);

            }, 10L, 10L);
        }
        catch (Exception ex) {
            throw new RuntimeException("Could not create boss bar", ex);
        }
    }

    private void updateBossBar(Player player) {
        try {
            Location witherLocation = player.getLocation().add(player.getLocation().getDirection().normalize().multiply(30));
            Object teleportPacket = PACKET_PLAY_OUT_ENTITY_TELEPORT_CONSTRUCTOR.newInstance(
                    this.witherId,
                    witherLocation.getBlockX() * 32,
                    witherLocation.getBlockY() * 32,
                    witherLocation.getBlockZ() * 32,
                    (byte) ((int) (witherLocation.getYaw() * 256 / 360)),
                    (byte) ((int) (witherLocation.getPitch() * 256 / 360)),
                    false
            );

            PacketSender.sendPacket(player, teleportPacket);
        }
        catch (Exception ex) {
            throw new RuntimeException("Could not send wither entity set location packet!", ex);
        }
    }

    private void removeBossBar(Player player) {
        try {
            Object destroyPacket = PACKET_PLAY_OUT_ENTITY_DESTROY_CONSTRUCTOR.newInstance(new int[] {this.witherId});
            PacketSender.sendPacket(player, destroyPacket);
            this.currentSecond.set(0);
        }
        catch (Exception ex) {
            throw new RuntimeException("Could not send wither entity destroy packet!", ex);
        }
    }
}

package net.dzikoysk.funnyguilds.nms;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class PacketSender {

    private static Method getHandle;
    private static Field playerConnection;
    private static Method sendPacket;

    static {
        try {
            getHandle = Reflections.getMethod(Reflections.getCraftBukkitClass("entity.CraftPlayer"), "getHandle");
            sendPacket = Reflections.getMethod(Reflections.getNMSClass("PlayerConnection"), "sendPacket");
            playerConnection = Reflections.getField(Reflections.getNMSClass("EntityPlayer"), "playerConnection");

        }
        catch (Exception ex) {
            FunnyGuilds.getPluginLogger().error("Could not initialize PacketSender class", ex);
        }
    }

    public static void sendPacket(final Collection<? extends Player> players, final Object... packets) {
        final List<Object> packetList = Arrays.asList(packets);
        players.forEach(p -> sendPacket(p, packetList));
    }

    public static void sendPacket(final Player[] players, final Object... packets) {
        final List<Object> packetList = Arrays.asList(packets);

        for (Player player : players) {
            sendPacket(player, packetList);
        }
    }

    public static void sendPacket(final Player target, final Object... packets) {
        sendPacket(target, Arrays.asList(packets));
    }

    public static void sendPacket(final List<Object> packets) {
        Bukkit.getOnlinePlayers().forEach(p -> sendPacket(p, packets));
    }

    public static void sendPacket(final Player target, final List<Object> packets) {
        if (target == null) {
            return;
        }

        try {
            final Object handle = getHandle.invoke(target);
            final Object connection = playerConnection.get(handle);
            
            for (Object packet : packets) {
                sendPacket.invoke(connection, packet);
            }
        } catch (IllegalAccessException | InvocationTargetException exception) {
            FunnyGuilds.getPluginLogger().error("Failed to send packets", exception);
        }
    }

    private PacketSender() {}

}
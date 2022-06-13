package net.dzikoysk.funnyguilds.nms;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import panda.std.stream.PandaStream;

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

    private PacketSender() {
    }

    public static void sendPacket(Collection<? extends Player> players, Object... packets) {
        List<Object> packetList = Arrays.asList(packets);
        players.forEach(player -> sendPacket(player, packetList));
    }

    public static void sendPacket(Player[] players, Object... packets) {
        List<Object> packetList = Arrays.asList(packets);
        PandaStream.of(players).forEach(player -> sendPacket(player, packetList));
    }

    public static void sendPacket(Player target, Object... packets) {
        sendPacket(target, Arrays.asList(packets));
    }

    public static void sendPacket(List<Object> packets) {
        Bukkit.getOnlinePlayers().forEach(player -> sendPacket(player, packets));
    }

    public static void sendPacket(Player target, List<Object> packets) {
        if (target == null) {
            return;
        }

        try {
            Object handle = getHandle.invoke(target);
            Object connection = playerConnection.get(handle);

            for (Object packet : packets) {
                sendPacket.invoke(connection, packet);
            }
        }
        catch (IllegalAccessException | InvocationTargetException exception) {
            FunnyGuilds.getPluginLogger().error("Failed to send packets", exception);
        }
    }

}
package net.dzikoysk.funnyguilds.util.reflect;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PacketSender {

    private static Method getHandle;
    private static Field playerConnection;
    private static Method sendPacket;

    static {
        try {
            getHandle = Reflections.getMethod(Reflections.getBukkitClass("entity.CraftPlayer"), "getHandle");
            sendPacket = Reflections.getMethod(Reflections.getCraftClass("PlayerConnection"), "sendPacket");
            playerConnection = Reflections.getField(Reflections.getCraftClass("EntityPlayer"), "playerConnection");

        } catch (Exception e) {
            if (FunnyGuilds.exception(e.getCause())) {
                e.printStackTrace();
            }
        }
    }

    public static void sendPacket(Collection<? extends Player> players, Object... packets) {
        List<Object> packetList = Arrays.asList(packets);
        players.forEach(p -> sendPacket(p, packetList));
    }

    public static void sendPacket(Player[] players, Object... packets) {

        List<Object> packetList = Arrays.asList(packets);

        for (Player player : players) {
            sendPacket(player, packetList);
        }
    }

    public static void sendPacket(Object... packets) {
        sendPacket(Arrays.asList(packets));
    }

    public static void sendPacket(Player target, Object... packets) {
        sendPacket(target, Arrays.asList(packets));
    }

    public static void sendPacket(List<Object> packets) {
        Bukkit.getOnlinePlayers().forEach(p -> sendPacket(p, packets));
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
        } catch (IllegalAccessException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }
}
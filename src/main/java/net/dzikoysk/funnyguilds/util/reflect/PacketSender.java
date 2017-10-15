package net.dzikoysk.funnyguilds.util.reflect;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

public class PacketSender {

    private static final String packageName = Bukkit.getServer().getClass().getPackage().getName();
    private static final String version = packageName.substring(packageName.lastIndexOf(".") + 1);
    private static Method getHandle;
    private static Field playerConnection;
    private static Method sendPacket;

    static {
        try {
            Class<?> packetClass = Class.forName("net.minecraft.server." + version + ".Packet");
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
            Class<?> entityPlayerClass = Class.forName("net.minecraft.server." + version + ".EntityPlayer");
            Class<?> playerConnectionClass = Class.forName("net.minecraft.server." + version + ".PlayerConnection");

            getHandle = craftPlayerClass.getMethod("getHandle");
            playerConnection = entityPlayerClass.getField("playerConnection");
            sendPacket = playerConnectionClass.getMethod("sendPacket", packetClass);

        } catch (Exception e) {
            if (FunnyGuilds.exception(e.getCause())) {
                e.printStackTrace();
            }
        }
    }

    public static void sendPacket(Player player, Object... os) {
        sendPacket(new Player[]{player}, os);
    }

    public static void sendPacket(Player[] players, Object... os) {
        sendPacket(Arrays.asList(players), os);
    }

    public static void sendPacket(Collection<? extends Player> players, Object... os) {
        try {
            for (Player p : players) {
                Object handle = getHandle.invoke(p);
                Object connection = playerConnection.get(handle);

                for (Object o : os) {
                    if (o == null) {
                        continue;
                    }
                    sendPacket.invoke(connection, o);
                }
            }
        } catch (Exception e) {
            if (FunnyGuilds.exception(e.getCause())) {
                e.printStackTrace();
            }
        }
    }

}
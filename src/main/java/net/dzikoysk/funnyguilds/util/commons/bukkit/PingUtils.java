package net.dzikoysk.funnyguilds.util.commons.bukkit;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.util.nms.Reflections;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PingUtils {

    private static Class<?> craftPlayerClass;
    private static Method   getHandleMethod;
    private static Field    pingField;

    static {
        craftPlayerClass = Reflections.getCraftBukkitClass("entity.CraftPlayer");
        getHandleMethod = Reflections.getMethod(craftPlayerClass, "getHandle");
        pingField = Reflections.getField(Reflections.getNMSClass("EntityPlayer"), "ping");
    }

    public static int getPing(Player player) {
        int ping = 0;

        if (player == null) {
            return ping;
        }

        try {
            Object cp = craftPlayerClass.cast(player);
            Object handle = getHandleMethod.invoke(cp);
            ping = (int) pingField.get(handle);
        }
        catch (Exception ex) {
            FunnyGuilds.getInstance().getPluginLogger().error("Could not retrieve player's ping", ex);
        }

        return ping;
    }

}

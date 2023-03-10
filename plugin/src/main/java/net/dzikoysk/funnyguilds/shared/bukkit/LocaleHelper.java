package net.dzikoysk.funnyguilds.shared.bukkit;

import java.lang.reflect.Method;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class LocaleHelper {

    private static Method GET_LOCALE_SPIGOT;

    static {
        try {
            GET_LOCALE_SPIGOT = Player.Spigot.class.getMethod("getLocale");
        } catch (NoSuchMethodException e) {
            GET_LOCALE_SPIGOT = null;
        }
    }

    @Nullable
    public static String getLocaleString(Player player) {
        if (GET_LOCALE_SPIGOT != null) {
            try {
                return (String) GET_LOCALE_SPIGOT.invoke(player.spigot());
            }
            catch (Exception ex) {
                return null;
            }
        }

        return player.getLocale();
    }

}

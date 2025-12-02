package net.dzikoysk.funnyguilds.shared.bukkit;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public final class LocaleHelper {

    private LocaleHelper() { }

    @Nullable
    public static String getLocaleString(Player player) {
        java.util.Locale locale = player.locale(); // nowoczesne API
        return locale != null ? locale.toString().toLowerCase() : null;
    }

}

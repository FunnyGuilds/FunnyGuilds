package net.dzikoysk.funnyguilds.config.message;

import dev.peri.yetanothermessageslibrary.locale.LocaleProvider;
import dev.peri.yetanothermessageslibrary.util.BukkitLocaleHelper;
import java.util.Locale;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerLocaleProvider implements LocaleProvider<Player> {

    @Override
    public boolean supports(@NotNull Class<?> clazz) {
        return Player.class.isAssignableFrom(clazz);
    }

    @Override
    public @Nullable Locale getLocale(@NotNull Player entity) {
        return BukkitLocaleHelper.getLocale(entity);
    }

}

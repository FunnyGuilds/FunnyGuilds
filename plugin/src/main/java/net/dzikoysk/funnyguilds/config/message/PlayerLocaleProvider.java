package net.dzikoysk.funnyguilds.config.message;

import java.util.Locale;
import net.dzikoysk.funnyguilds.shared.bukkit.LocaleHelper;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import dev.peri.yetanothermessageslibrary.locale.LocaleProvider;

public class PlayerLocaleProvider implements LocaleProvider<Player> {

    @Override
    public boolean supports(@NotNull Class<?> clazz) {
        return Player.class.isAssignableFrom(clazz);
    }

    @Override
    public @Nullable Locale getLocale(@NotNull Player entity) {
        String localeString = LocaleHelper.getLocaleString(entity);
        if (localeString == null) {
            return null;
        }

        localeString = localeString.replace('_', '-');
        return Locale.forLanguageTag(localeString);
    }

}

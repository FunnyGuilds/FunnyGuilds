package net.dzikoysk.funnyguilds.config.message;

import java.util.Locale;
import net.dzikoysk.funnyguilds.shared.bukkit.FunnyServer;
import net.dzikoysk.funnyguilds.shared.bukkit.LocaleHelper;
import net.dzikoysk.funnyguilds.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import dev.peri.yetanothermessageslibrary.locale.LocaleProvider;

public class UserLocaleProvider implements LocaleProvider<User> {

    private final FunnyServer funnyServer;

    public UserLocaleProvider(FunnyServer funnyServer) {
        this.funnyServer = funnyServer;
    }

    @Override
    public boolean supports(@NotNull Class<?> clazz) {
        return User.class.isAssignableFrom(clazz);
    }

    @Override
    public @Nullable Locale getLocale(@NotNull User entity) {
        return this.funnyServer.getPlayer(entity)
                .map(LocaleHelper::getLocaleString)
                .map(localeString -> localeString.replace('_', '-'))
                .map(Locale::forLanguageTag)
                .orNull();
    }

}

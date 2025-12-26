package net.dzikoysk.funnyguilds.config.message;

import java.util.Locale;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface EntityLocaleProvider {
    
    Locale getEntityLocale(@Nullable Object entity);
}

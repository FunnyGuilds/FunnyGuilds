package net.dzikoysk.funnyguilds.config.message;

import dev.peri.yetanothermessageslibrary.replace.Replaceable;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface EntityLocaleProvider {
    
    Locale getEntityLocale(@Nullable Object entity);
    
    default Component replaceInComponent(Object entity, Component component, Replaceable replaceable) {
        Locale locale = this.getEntityLocale(entity);
        return replaceable.replace(locale, component);
    }
}

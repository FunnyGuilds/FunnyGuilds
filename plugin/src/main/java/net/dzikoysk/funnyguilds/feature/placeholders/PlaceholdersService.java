package net.dzikoysk.funnyguilds.feature.placeholders;

import dev.peri.yetanothermessageslibrary.replace.Replaceable;
import java.util.Locale;
import java.util.function.UnaryOperator;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;


public interface PlaceholdersService<T> {

    default Component format(
            Object entity,
            Component text,
            T data
    ) {
        Replaceable formatter = this.asReplaceable(data);
        Locale locale = this.getEntityLocale(entity);
        return formatter.replace(locale, text);
    }
    
    default Component format(
            Object entity,
            Component text,
            T data,
            String prefix,
            String suffix,
            UnaryOperator<String> nameModifier
    ) {
        Replaceable formatter = this.asReplaceable(data, prefix, suffix, nameModifier);
        Locale locale = this.getEntityLocale(entity);
        return formatter.replace(locale, text);
    }
    
    default Replaceable asReplaceable(T data) {
        return this.asReplaceable(data, "{", "}", name -> name.toLowerCase(Locale.ROOT));
    }
    
    Replaceable asReplaceable(T data, String prefix, String suffix, UnaryOperator<String> nameModifier);
    
    @ApiStatus.Internal
    Locale getEntityLocale(@Nullable Object entity);
}

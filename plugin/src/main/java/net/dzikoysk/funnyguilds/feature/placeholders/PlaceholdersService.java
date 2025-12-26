package net.dzikoysk.funnyguilds.feature.placeholders;

import java.util.Locale;
import java.util.function.UnaryOperator;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;


public interface PlaceholdersService<T> {

    default Component format(
            Object entity,
            Component text,
            T data
    ) {
        FunnyFormatter formatter = this.toFormatter(data);
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
        FunnyFormatter formatter = this.toFormatter(data, prefix, suffix, nameModifier);
        Locale locale = this.getEntityLocale(entity);
        return formatter.replace(locale, text);
    }
    
    default FunnyFormatter toFormatter(T data) {
        return this.toFormatter(data, "{", "}", name -> name.toLowerCase(Locale.ROOT));
    }
    
    FunnyFormatter toFormatter(T data, String prefix, String suffix, UnaryOperator<String> nameModifier);
    
    @ApiStatus.Internal
    Locale getEntityLocale(@Nullable Object entity);
}

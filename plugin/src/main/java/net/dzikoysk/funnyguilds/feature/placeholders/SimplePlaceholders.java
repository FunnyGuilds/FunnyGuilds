package net.dzikoysk.funnyguilds.feature.placeholders;

import java.time.OffsetDateTime;
import java.util.Objects;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.MonoResolver;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;

public class SimplePlaceholders<T> extends Placeholders<T, SimplePlaceholders<T>> {

    public SimplePlaceholders<T> timeProperty(String name, MonoResolver<OffsetDateTime> timeResolver) {
        return this.property(name, (data) -> {
            if (!(data instanceof OffsetDateTime)) {
                return "";
            }
            OffsetDateTime time = (OffsetDateTime) data;
            return ChatUtils.appendDigit(Objects.toString(timeResolver.resolve(time)));
        });
    }

    @Override
    public SimplePlaceholders<T> create() {
        return new SimplePlaceholders<>();
    }

}

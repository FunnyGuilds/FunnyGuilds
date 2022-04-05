package net.dzikoysk.funnyguilds.feature.placeholders;

import java.time.OffsetDateTime;
import java.util.Objects;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.MonoResolver;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;

public class OffsetDateTimePlaceholders extends Placeholders<OffsetDateTime, OffsetDateTimePlaceholders> {

    public OffsetDateTimePlaceholders timeProperty(String name, MonoResolver<OffsetDateTime> timeResolver) {
        return this.property(name, (data) -> ChatUtils.appendDigit(Objects.toString(timeResolver.resolve(data))));
    }

    @Override
    public OffsetDateTimePlaceholders create() {
        return new OffsetDateTimePlaceholders();
    }

}

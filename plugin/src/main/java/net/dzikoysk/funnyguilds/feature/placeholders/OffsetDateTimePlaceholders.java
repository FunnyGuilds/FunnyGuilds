package net.dzikoysk.funnyguilds.feature.placeholders;

import java.time.OffsetDateTime;
import java.util.Objects;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.MonoResolver;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;

public class OffsetDateTimePlaceholders extends Placeholders<OffsetDateTime, OffsetDateTimePlaceholders> {

    public OffsetDateTimePlaceholders timeProperty(String name, MonoResolver<OffsetDateTime> timeResolver) {
        return this.property(name, (data) -> FunnyStringUtils.appendDigit(Objects.toString(timeResolver.resolve(
                OffsetDateTime.now(FunnyGuilds.getInstance().getPluginConfiguration().timeZone)
        ))));
    }

    @Override
    public OffsetDateTimePlaceholders create() {
        return new OffsetDateTimePlaceholders();
    }

}

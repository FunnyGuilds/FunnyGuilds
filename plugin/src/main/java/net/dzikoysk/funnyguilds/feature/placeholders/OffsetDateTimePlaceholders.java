package net.dzikoysk.funnyguilds.feature.placeholders;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Objects;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.MonoResolver;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;

public class OffsetDateTimePlaceholders extends Placeholders<OffsetDateTime, OffsetDateTimePlaceholders> {

    protected final ZoneId timeZone;

    public OffsetDateTimePlaceholders(ZoneId timeZone) {
        this.timeZone = timeZone;
    }

    public OffsetDateTimePlaceholders timeProperty(String name, MonoResolver<OffsetDateTime> timeResolver) {
        return this.property(name, (data) -> FunnyStringUtils.appendDigit(
                Objects.toString(timeResolver.resolve(
                        OffsetDateTime.now(this.timeZone)
                ))
        ));
    }

    @Override
    public OffsetDateTimePlaceholders create() {
        return new OffsetDateTimePlaceholders(this.timeZone);
    }

}

package net.dzikoysk.funnyguilds.feature.placeholders;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;

public class OffsetDateTimePlaceholders extends Placeholders<OffsetDateTime, OffsetDateTimePlaceholders> {

    public OffsetDateTimePlaceholders timeProperty(String name, Function<OffsetDateTime, Object> timeSupplier) {
        return this.property(name, (entity, data) -> FunnyStringUtils.appendDigit(Objects.toString(timeSupplier.apply(data))));
    }

    @Override
    public OffsetDateTimePlaceholders create() {
        return new OffsetDateTimePlaceholders();
    }

}

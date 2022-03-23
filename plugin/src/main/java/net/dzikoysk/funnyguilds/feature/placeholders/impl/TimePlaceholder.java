package net.dzikoysk.funnyguilds.feature.placeholders.impl;

import java.time.LocalDateTime;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.TimeResolver;

public class TimePlaceholder implements Placeholder<LocalDateTime> {

    private final TimeResolver timeFormatter;

    public TimePlaceholder(TimeResolver timeFormatter) {
        this.timeFormatter = timeFormatter;
    }

    @Override
    public Object getRaw(LocalDateTime time) {
        return this.timeFormatter.resolve(time);
    }

}

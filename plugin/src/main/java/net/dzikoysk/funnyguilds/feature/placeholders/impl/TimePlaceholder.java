package net.dzikoysk.funnyguilds.feature.placeholders.impl;

import java.time.LocalDateTime;
import net.dzikoysk.funnyguilds.feature.placeholders.Placeholder;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.TimeResolver;
import net.dzikoysk.funnyguilds.user.User;
import panda.std.Option;

public class TimePlaceholder implements Placeholder<User> {

    private final TimeResolver timeFormatter;

    private Option<LocalDateTime> currentTime;

    public TimePlaceholder(TimeResolver timeFormatter) {
        this.timeFormatter = timeFormatter;
    }

    public void supplyCurrentTime(LocalDateTime time) {
        this.currentTime = Option.of(time);
    }

    @Override
    public Object getRaw(User user) {
        return this.timeFormatter.resolve(currentTime.orElseGet(LocalDateTime.now()));
    }

}

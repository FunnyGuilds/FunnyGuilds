package net.dzikoysk.funnyguilds.feature.placeholders.resolver;

import java.time.LocalDateTime;

@FunctionalInterface
public interface TimeResolver {
    Object resolve(LocalDateTime time);
}

package net.dzikoysk.funnyguilds;

import java.util.Set;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.shared.FunnyStreams;

public interface Entity {

    EntityType getType();

    String getName();

    static <T extends Entity> Set<String> names(Iterable<T> entities) {
        return FunnyStreams.of(entities)
                .map(Entity::getName)
                .collect(Collectors.toUnmodifiableSet());
    }

    enum EntityType {

        GUILD,
        OFFLINE_USER,
        RANK,
        REGION,
        USER

    }

}

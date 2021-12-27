package net.dzikoysk.funnyguilds;

import java.util.Set;
import panda.std.stream.PandaStream;

public interface Entity {

    enum EntityType {
        GUILD,
        OFFLINE_USER,
        RANK,
        REGION,
        USER
    }

    EntityType getType();

    String getName();

    static <T extends Entity> Set<String> names(Iterable<T> entities) {
        return PandaStream.of(entities)
                .map(Entity::getName)
                .toSet();
    }

}

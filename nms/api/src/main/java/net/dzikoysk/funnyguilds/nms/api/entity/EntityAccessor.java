package net.dzikoysk.funnyguilds.nms.api.entity;

import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public interface EntityAccessor {
    FakeEntity createFakeEntity(EntityType entityType, Location location);

    void spawnFakeEntityFor(FakeEntity entity, Player... players);

    default void spawnFakeEntityFor(FakeEntity entity, Collection<? extends Player> players) {
        players.forEach(player -> this.spawnFakeEntityFor(entity, player));
    }

    void despawnFakeEntityFor(FakeEntity entity, Player... players);

    default void despawnFakeEntityFor(FakeEntity entity, Collection<? extends Player> players) {
        players.forEach(player -> this.despawnFakeEntityFor(entity, player));
    }

}

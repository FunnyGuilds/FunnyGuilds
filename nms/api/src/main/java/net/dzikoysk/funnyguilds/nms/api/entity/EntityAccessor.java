package net.dzikoysk.funnyguilds.nms.api.entity;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public interface EntityAccessor {
    FakeEntity createFakeEntity(EntityType entityType, Location location);

    void spawnFakeEntityFor(FakeEntity entity, Player... players);

    void despawnFakeEntityFor(FakeEntity entity, Player... players);
}

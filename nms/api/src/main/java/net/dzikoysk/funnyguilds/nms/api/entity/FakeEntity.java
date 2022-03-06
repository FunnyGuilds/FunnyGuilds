package net.dzikoysk.funnyguilds.nms.api.entity;

import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class FakeEntity {

    private final EntityType entityType;
    private final Location location;

    private final int id;
    private final Object spawnPacket;

    public FakeEntity(EntityType entityType, Location location, int id, Object spawnPacket) {
        this.entityType = entityType;
        this.location = location;
        this.id = id;
        this.spawnPacket = spawnPacket;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public Location getLocation() {
        return location;
    }

    public int[] getChunkCoordinates() {
        return new int[] {location.getBlockX() >> 4, location.getBlockZ() >> 4};
    }

    public int getId() {
        return this.id;
    }

    public Object getSpawnPacket() {
        return this.spawnPacket;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FakeEntity that = (FakeEntity) o;
        return this.id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

}

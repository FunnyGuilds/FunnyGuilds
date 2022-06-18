package net.dzikoysk.funnyguilds.nms.api.entity;

import org.bukkit.Location;

public class FakeEntity {

    private final int id;
    private final Location location;
    private final Object spawnPacket;

    public FakeEntity(int id, Location location, Object spawnPacket) {
        this.id = id;
        this.location = location;
        this.spawnPacket = spawnPacket;
    }

    public int getId() {
        return this.id;
    }

    public Location getLocation() {
        return this.location;
    }

    public int getChunkX() {
        return this.location.getBlockX() >> 4;
    }

    public int getChunkZ() {
        return this.location.getBlockZ() >> 4;
    }

    public Object getSpawnPacket() {
        return this.spawnPacket;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        FakeEntity that = (FakeEntity) o;
        return this.id == that.id;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

}

package net.dzikoysk.funnyguilds.nms.api.entity;

import java.util.Objects;

public class FakeEntity {
    private final int id;
    private final Object spawnPacket;

    public FakeEntity(int id, Object spawnPacket) {
        this.id = id;
        this.spawnPacket = spawnPacket;
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

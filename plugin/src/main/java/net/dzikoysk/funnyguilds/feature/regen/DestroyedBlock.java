package net.dzikoysk.funnyguilds.feature.regen;

import java.time.Instant;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a destroyed block that can be regenerated.
 */
public class DestroyedBlock {

    private final Location location;
    private final Material material;
    private final BlockData blockData;
    private final Instant destroyedAt;
    @Nullable
    private final String tileEntityData;

    public DestroyedBlock(Location location, Material material, BlockData blockData, @Nullable String tileEntityData) {
        this.location = location.clone();
        this.material = material;
        this.blockData = blockData.clone();
        this.destroyedAt = Instant.now();
        this.tileEntityData = tileEntityData;
    }

    public Location getLocation() {
        return this.location.clone();
    }

    public Material getMaterial() {
        return this.material;
    }

    public BlockData getBlockData() {
        return this.blockData.clone();
    }

    public Instant getDestroyedAt() {
        return this.destroyedAt;
    }

    @Nullable
    public String getTileEntityData() {
        return this.tileEntityData;
    }

    public boolean hasTileEntityData() {
        return this.tileEntityData != null && !this.tileEntityData.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DestroyedBlock that = (DestroyedBlock) obj;
        return this.location.equals(that.location);
    }

    @Override
    public int hashCode() {
        return this.location.hashCode();
    }

    @Override
    public String toString() {
        return "DestroyedBlock{" +
                "location=" + this.location +
                ", material=" + this.material +
                ", destroyedAt=" + this.destroyedAt +
                '}';
    }
}

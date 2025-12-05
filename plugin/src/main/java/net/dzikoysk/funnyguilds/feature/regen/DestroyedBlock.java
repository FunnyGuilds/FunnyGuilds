package net.dzikoysk.funnyguilds.feature.regen;

import java.time.Duration;
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

    /**
     * Creates a new DestroyedBlock with the current time as destruction time.
     */
    public DestroyedBlock(Location location, Material material, BlockData blockData, @Nullable String tileEntityData) {
        this(location, material, blockData, Instant.now(), tileEntityData);
    }

    /**
     * Creates a new DestroyedBlock with a specific destruction time (for deserialization).
     */
    public DestroyedBlock(Location location, Material material, BlockData blockData, Instant destroyedAt, @Nullable String tileEntityData) {
        this.location = location.clone();
        this.material = material;
        this.blockData = blockData.clone();
        this.destroyedAt = destroyedAt;
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

    /**
     * Checks if this block is expired based on the maximum age.
     *
     * @param maxAge The maximum age for blocks, or null/zero to disable expiration
     * @return true if the block is expired and should not be regenerated
     */
    public boolean isExpired(@Nullable Duration maxAge) {
        if (maxAge == null || maxAge.isZero() || maxAge.isNegative()) {
            return false;
        }
        Instant expirationTime = this.destroyedAt.plus(maxAge);
        return Instant.now().isAfter(expirationTime);
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

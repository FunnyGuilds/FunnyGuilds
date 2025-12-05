package net.dzikoysk.funnyguilds.feature.regen;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.guild.Guild;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.jetbrains.annotations.Nullable;

/**
 * Manages destroyed blocks for all guilds that can be regenerated.
 */
public class RegionRegenerationManager {

    private final Map<UUID, List<DestroyedBlock>> destroyedBlocksByGuild = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> regenerationInProgress = new ConcurrentHashMap<>();
    private final Map<UUID, Instant> lastRegenerationTime = new ConcurrentHashMap<>();

    /**
     * Registers a destroyed block for a guild.
     *
     * @param guild The guild whose region the block was in
     * @param block The block that was destroyed
     */
    public void registerDestroyedBlock(Guild guild, Block block) {
        UUID guildId = guild.getUUID();
        
        String tileEntityData = extractTileEntityData(block);
        DestroyedBlock destroyedBlock = new DestroyedBlock(
                block.getLocation(),
                block.getType(),
                block.getBlockData(),
                tileEntityData
        );

        this.destroyedBlocksByGuild
                .computeIfAbsent(guildId, k -> new CopyOnWriteArrayList<>())
                .add(destroyedBlock);
    }

    /**
     * Registers multiple destroyed blocks for a guild.
     *
     * @param guild The guild whose region the blocks were in
     * @param blocks The blocks that were destroyed
     */
    public void registerDestroyedBlocks(Guild guild, Collection<Block> blocks) {
        for (Block block : blocks) {
            registerDestroyedBlock(guild, block);
        }
    }

    /**
     * Gets the number of destroyed blocks for a guild that can be regenerated.
     * This method also removes expired blocks based on the maxBlockAge parameter.
     *
     * @param guild The guild
     * @param maxBlockAge The maximum age for blocks, or null to disable expiration
     * @return Number of destroyed blocks that are still valid
     */
    public int getDestroyedBlockCount(Guild guild, @Nullable Duration maxBlockAge) {
        removeExpiredBlocks(guild, maxBlockAge);
        List<DestroyedBlock> blocks = this.destroyedBlocksByGuild.get(guild.getUUID());
        return blocks != null ? blocks.size() : 0;
    }

    /**
     * Gets the number of destroyed blocks for a guild (without expiration check).
     *
     * @param guild The guild
     * @return Number of destroyed blocks
     */
    public int getDestroyedBlockCount(Guild guild) {
        return getDestroyedBlockCount(guild, null);
    }

    /**
     * Removes expired blocks from a guild's list.
     *
     * @param guild The guild
     * @param maxBlockAge The maximum age for blocks, or null to skip removal
     */
    public void removeExpiredBlocks(Guild guild, @Nullable Duration maxBlockAge) {
        if (maxBlockAge == null || maxBlockAge.isZero() || maxBlockAge.isNegative()) {
            return;
        }
        
        List<DestroyedBlock> guildBlocks = this.destroyedBlocksByGuild.get(guild.getUUID());
        if (guildBlocks != null) {
            guildBlocks.removeIf(block -> block.isExpired(maxBlockAge));
        }
    }

    /**
     * Gets destroyed blocks for a guild, sorted by destruction time (oldest first).
     * This method also removes expired blocks based on the maxBlockAge parameter.
     *
     * @param guild The guild
     * @param maxBlockAge The maximum age for blocks, or null to disable expiration
     * @return List of destroyed blocks that are still valid
     */
    public List<DestroyedBlock> getDestroyedBlocks(Guild guild, @Nullable Duration maxBlockAge) {
        removeExpiredBlocks(guild, maxBlockAge);
        List<DestroyedBlock> blocks = this.destroyedBlocksByGuild.get(guild.getUUID());
        if (blocks == null || blocks.isEmpty()) {
            return new ArrayList<>();
        }
        return blocks.stream()
                .sorted(Comparator.comparing(DestroyedBlock::getDestroyedAt))
                .collect(Collectors.toList());
    }

    /**
     * Gets destroyed blocks for a guild, sorted by destruction time (oldest first).
     *
     * @param guild The guild
     * @return List of destroyed blocks
     */
    public List<DestroyedBlock> getDestroyedBlocks(Guild guild) {
        return getDestroyedBlocks(guild, null);
    }

    /**
     * Gets a specified number of destroyed blocks for regeneration.
     *
     * @param guild The guild
     * @param count The number of blocks to get
     * @param maxBlockAge The maximum age for blocks, or null to disable expiration
     * @return List of destroyed blocks to regenerate
     */
    public List<DestroyedBlock> getBlocksForRegeneration(Guild guild, int count, @Nullable Duration maxBlockAge) {
        List<DestroyedBlock> blocks = getDestroyedBlocks(guild, maxBlockAge);
        return blocks.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    /**
     * Gets a specified number of destroyed blocks for regeneration.
     *
     * @param guild The guild
     * @param count The number of blocks to get
     * @return List of destroyed blocks to regenerate
     */
    public List<DestroyedBlock> getBlocksForRegeneration(Guild guild, int count) {
        return getBlocksForRegeneration(guild, count, null);
    }

    /**
     * Removes regenerated blocks from the tracking list.
     *
     * @param guild The guild
     * @param blocks The blocks that were regenerated
     */
    public void removeRegeneratedBlocks(Guild guild, Collection<DestroyedBlock> blocks) {
        List<DestroyedBlock> guildBlocks = this.destroyedBlocksByGuild.get(guild.getUUID());
        if (guildBlocks != null) {
            guildBlocks.removeAll(blocks);
        }
    }

    /**
     * Removes a single block from tracking (e.g., if someone placed a block at that location).
     *
     * @param guild The guild
     * @param location The location to remove
     * @return true if a block was removed
     */
    public boolean removeBlockAtLocation(Guild guild, Location location) {
        List<DestroyedBlock> guildBlocks = this.destroyedBlocksByGuild.get(guild.getUUID());
        if (guildBlocks != null) {
            return guildBlocks.removeIf(block -> block.getLocation().equals(location));
        }
        return false;
    }

    /**
     * Checks if regeneration is in progress for a guild.
     *
     * @param guild The guild
     * @return true if regeneration is in progress
     */
    public boolean isRegenerationInProgress(Guild guild) {
        return this.regenerationInProgress.getOrDefault(guild.getUUID(), false);
    }

    /**
     * Sets the regeneration progress state for a guild.
     *
     * @param guild The guild
     * @param inProgress true if regeneration is starting, false if finished
     */
    public void setRegenerationInProgress(Guild guild, boolean inProgress) {
        this.regenerationInProgress.put(guild.getUUID(), inProgress);
        if (!inProgress) {
            this.lastRegenerationTime.put(guild.getUUID(), Instant.now());
        }
    }

    /**
     * Gets the last regeneration time for a guild.
     *
     * @param guild The guild
     * @return The last regeneration time, or null if never regenerated
     */
    @Nullable
    public Instant getLastRegenerationTime(Guild guild) {
        return this.lastRegenerationTime.get(guild.getUUID());
    }

    /**
     * Clears all destroyed blocks for a guild (e.g., when guild is deleted).
     *
     * @param guild The guild
     */
    public void clearGuildData(Guild guild) {
        UUID guildId = guild.getUUID();
        this.destroyedBlocksByGuild.remove(guildId);
        this.regenerationInProgress.remove(guildId);
        this.lastRegenerationTime.remove(guildId);
    }

    /**
     * Extracts tile entity data from a block if applicable.
     * 
     * <p><b>Limitations:</b> The current implementation only marks blocks as having tile entity data
     * but does not preserve the actual contents. This means:
     * <ul>
     *     <li>Chests will be regenerated as empty chests</li>
     *     <li>Signs will be regenerated without text</li>
     *     <li>Furnaces will be regenerated without items or smelting progress</li>
     *     <li>Other tile entities will similarly lose their data</li>
     * </ul>
     * Full tile entity restoration would require NBT data serialization, which is complex
     * and version-dependent. This is a known limitation of the regeneration system.
     *
     * @param block The block
     * @return Tile entity data marker as string, or null if not a tile entity
     */
    @Nullable
    private String extractTileEntityData(Block block) {
        BlockState state = block.getState();
        if (state instanceof TileState) {
            // Mark that this block had tile entity data
            // Note: Actual tile entity contents are NOT preserved
            return "tile_entity";
        }
        return null;
    }
}

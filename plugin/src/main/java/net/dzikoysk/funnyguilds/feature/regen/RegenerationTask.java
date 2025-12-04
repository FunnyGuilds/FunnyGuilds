package net.dzikoysk.funnyguilds.feature.regen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.sections.PanelConfiguration.RegenerationConfig;
import net.dzikoysk.funnyguilds.guild.Guild;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;

/**
 * Handles asynchronous regeneration of destroyed blocks.
 */
public class RegenerationTask implements Runnable {

    private final FunnyGuilds plugin;
    private final RegionRegenerationManager regenerationManager;
    private final Guild guild;
    private final List<DestroyedBlock> blocksToRegenerate;
    private final int batchSize;
    private final Consumer<RegenerationResult> callback;

    private int currentIndex = 0;
    private int regeneratedCount = 0;
    private int skippedCount = 0;
    private BukkitTask task;

    public RegenerationTask(
            FunnyGuilds plugin,
            RegionRegenerationManager regenerationManager,
            Guild guild,
            List<DestroyedBlock> blocksToRegenerate,
            int batchSize,
            Consumer<RegenerationResult> callback
    ) {
        this.plugin = plugin;
        this.regenerationManager = regenerationManager;
        this.guild = guild;
        this.blocksToRegenerate = new ArrayList<>(blocksToRegenerate);
        this.batchSize = batchSize;
        this.callback = callback;
    }

    private static final long TASK_INTERVAL_TICKS = 2L; // Run every 2 ticks for balance between speed and performance

    /**
     * Starts the regeneration task.
     */
    public void start() {
        this.regenerationManager.setRegenerationInProgress(this.guild, true);
        // Run synchronously since we need to modify blocks
        this.task = Bukkit.getScheduler().runTaskTimer(this.plugin, this, 1L, TASK_INTERVAL_TICKS);
    }

    /**
     * Stops the regeneration task.
     */
    public void stop() {
        if (this.task != null && !this.task.isCancelled()) {
            this.task.cancel();
        }
        this.regenerationManager.setRegenerationInProgress(this.guild, false);
    }

    @Override
    public void run() {
        if (this.currentIndex >= this.blocksToRegenerate.size()) {
            // Regeneration complete
            finishRegeneration();
            return;
        }

        int endIndex = Math.min(this.currentIndex + this.batchSize, this.blocksToRegenerate.size());
        List<DestroyedBlock> regeneratedBlocks = new ArrayList<>();

        for (int i = this.currentIndex; i < endIndex; i++) {
            DestroyedBlock destroyedBlock = this.blocksToRegenerate.get(i);
            boolean success = regenerateBlock(destroyedBlock);
            
            if (success) {
                this.regeneratedCount++;
                regeneratedBlocks.add(destroyedBlock);
            } else {
                this.skippedCount++;
            }
        }

        // Remove successfully regenerated blocks from tracking
        this.regenerationManager.removeRegeneratedBlocks(this.guild, regeneratedBlocks);

        this.currentIndex = endIndex;
    }

    /**
     * Regenerates a single block.
     *
     * @param destroyedBlock The block to regenerate
     * @return true if successfully regenerated, false if skipped
     */
    private boolean regenerateBlock(DestroyedBlock destroyedBlock) {
        Location location = destroyedBlock.getLocation();
        
        // Check if world is loaded
        if (location.getWorld() == null) {
            return false;
        }

        Block block = location.getBlock();
        
        // Check if block is already placed (not air or different material)
        if (block.getType() != Material.AIR && block.getType() != Material.CAVE_AIR) {
            // Block was already placed by someone, skip it
            return false;
        }

        // Set the block type and data
        block.setBlockData(destroyedBlock.getBlockData(), false);

        // Handle tile entities (basic restoration)
        if (destroyedBlock.hasTileEntityData()) {
            // For now, we just restore the block type
            // Full tile entity restoration would require more complex NBT handling
            // This could be expanded in the future
        }

        return true;
    }

    /**
     * Finishes the regeneration process and calls the callback.
     */
    private void finishRegeneration() {
        stop();

        RegenerationResult result = new RegenerationResult(
                this.regeneratedCount,
                this.skippedCount,
                this.blocksToRegenerate.size()
        );

        if (this.callback != null) {
            this.callback.accept(result);
        }
    }

    /**
     * Gets the current progress percentage.
     *
     * @return Progress percentage (0-100)
     */
    public int getProgressPercentage() {
        if (this.blocksToRegenerate.isEmpty()) {
            return 100;
        }
        return (this.currentIndex * 100) / this.blocksToRegenerate.size();
    }

    /**
     * Result of a regeneration operation.
     */
    public static class RegenerationResult {

        private final int regeneratedCount;
        private final int skippedCount;
        private final int totalRequested;

        public RegenerationResult(int regeneratedCount, int skippedCount, int totalRequested) {
            this.regeneratedCount = regeneratedCount;
            this.skippedCount = skippedCount;
            this.totalRequested = totalRequested;
        }

        public int getRegeneratedCount() {
            return this.regeneratedCount;
        }

        public int getSkippedCount() {
            return this.skippedCount;
        }

        public int getTotalRequested() {
            return this.totalRequested;
        }

        public boolean isSuccess() {
            return this.regeneratedCount > 0;
        }
    }
}

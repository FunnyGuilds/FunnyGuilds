package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.feature.protection.ProtectionSystem;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.SpongeAbsorbEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.StructureGrowEvent;
import panda.std.stream.PandaStream;

public class GuildHeartProtectionHandler extends AbstractFunnyListener {

    private static boolean isBucketItem(Material material) {
        String name = material.name();
        return name.equals("BUCKET") || name.endsWith("_BUCKET");
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        PandaStream.of(event.getBlocks())
                .find(block -> this.regionManager.isGuildHeart(block))
                .peek(block -> event.setCancelled(true));
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        PandaStream.of(event.getBlocks())
                .find(block -> this.regionManager.isGuildHeart(block))
                .peek(block -> event.setCancelled(true));
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        if (this.regionManager.isGuildHeart(event.getBlock())
                || ProtectionSystem.isGuildHeartProtectedRegion(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        if (this.regionManager.isGuildHeart(event.getBlock())
                || ProtectionSystem.isGuildHeartProtectedRegion(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        if (this.regionManager.isGuildHeart(event.getBlock())) {
            event.setCancelled(true);
            return;
        }

        event.blockList().removeIf(block -> ProtectionSystem.isGuildHeartProtectedRegion(block.getLocation()));
    }

    @EventHandler
    public void onLiquidFlow(BlockFromToEvent event) {
        if (ProtectionSystem.isGuildHeartProtectedRegion(event.getToBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        if (ProtectionSystem.isGuildHeartProtectedRegion(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        if (ProtectionSystem.isGuildHeartProtectedRegion(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockGrow(BlockGrowEvent event) {
        if (ProtectionSystem.isGuildHeartProtectedRegion(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onStructureGrow(StructureGrowEvent event) {
        event.getBlocks().removeIf(state -> ProtectionSystem.isGuildHeartProtectedRegion(state.getLocation()));
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        if (ProtectionSystem.isGuildHeartProtectedRegion(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPortalCreate(PortalCreateEvent event) {
        boolean intersects = PandaStream.of(event.getBlocks())
                .find(state -> ProtectionSystem.isGuildHeartProtectedRegion(state.getLocation()))
                .isDefined();
        if (intersects) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockDispense(BlockDispenseEvent event) {
        if (!isBucketItem(event.getItem().getType())) {
            return;
        }

        Block source = event.getBlock();
        BlockData data = source.getBlockData();
        if (!(data instanceof Directional)) {
            return;
        }

        Block target = source.getRelative(((Directional) data).getFacing());
        if (ProtectionSystem.isGuildHeartProtectedRegion(target.getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpongeAbsorb(SpongeAbsorbEvent event) {
        if (ProtectionSystem.isGuildHeartProtectedRegion(event.getBlock().getLocation())) {
            event.setCancelled(true);
            return;
        }

        boolean absorbsCuboid = PandaStream.of(event.getBlocks())
                .find(state -> ProtectionSystem.isGuildHeartProtectedRegion(state.getLocation()))
                .isDefined();
        if (absorbsCuboid) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onNonPlayerIgnite(BlockIgniteEvent event) {
        if (event.getPlayer() != null) {
            return;
        }

        if (ProtectionSystem.isGuildHeartProtectedRegion(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

}

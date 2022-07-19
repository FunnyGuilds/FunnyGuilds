package net.dzikoysk.funnyguilds.listener;

import java.util.List;
import net.dzikoysk.funnyguilds.feature.protection.ProtectionSystem;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import panda.std.stream.PandaStream;

public class PistonUse extends AbstractFunnyListener {

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        this.handlePiston(event, event.getBlocks(), false);
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        this.handlePiston(event, event.getBlocks(), true);
    }

    public void handlePiston(BlockPistonEvent event, List<Block> blocks, boolean retract) {
        BlockFace direction = event.getDirection();

        PandaStream.of(blocks)
                .map(block -> retract
                        ? block
                        : block.getRelative(direction))
                .map(Block::getLocation)
                .find(ProtectionSystem::isGuildHeartProtectedRegion)
                .peek(location -> event.setCancelled(true));

        PandaStream.of(event.getBlock().getRelative(direction))
                .map(Block::getLocation)
                .find(ProtectionSystem::isGuildHeartProtectedRegion)
                .peek(location -> event.setCancelled(true));
    }

}

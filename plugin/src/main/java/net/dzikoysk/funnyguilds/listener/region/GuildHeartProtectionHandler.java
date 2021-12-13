package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.guild.RegionManager;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

public class GuildHeartProtectionHandler extends AbstractFunnyListener {

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (isGuildHeart(this.config, this.regionManager, block)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (isGuildHeart(this.config, this.regionManager, block)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        if (isGuildHeart(this.config, this.regionManager, event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        if (isGuildHeart(this.config, this.regionManager, event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        if (isGuildHeart(this.config, this.regionManager, event.getBlock())) {
            event.setCancelled(true);
        }
    }

    public static boolean isGuildHeart(PluginConfiguration config, RegionManager regionManager, Block block) {
        Pair<Material, Byte> md = config.heart.createMaterial;
        if (md == null || block.getType() != md.getLeft()) {
            return false;
        }

        Location blockLocation = block.getLocation();

        return regionManager.findRegionAtLocation(blockLocation)
                .map(region -> blockLocation.equals(region.getHeart()))
                .orElseGet(false);
    }
}

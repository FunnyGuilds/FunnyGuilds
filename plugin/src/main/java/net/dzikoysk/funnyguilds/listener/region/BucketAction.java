package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.feature.protection.ProtectionSystem;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

public class BucketAction extends AbstractFunnyListener {

    @EventHandler
    public void onFill(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockClicked();
        Material type = block.getType();
        Location blockLocation = block.getLocation();

        boolean isProtected = ProtectionSystem.isProtected(player, blockLocation, true)
                .peek(ProtectionSystem::defaultResponse)
                .isPresent();

        if (!isProtected) {
            return;
        }

        if (config.placingBlocksBypassOnRegion.contains(type)) {
            return;
        }

        // always cancel to prevent breaking other protection
        // plugins or plugins using PlayerBucketFillEvent (eg. special ability blocks)
        event.setCancelled(true);
    }

    @EventHandler
    public void onEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockClicked();
        Material type = event.getBucket();
        Location blockLocation = block.getLocation();

        boolean isProtected = ProtectionSystem.isProtected(player, blockLocation, true)
                .peek(ProtectionSystem::defaultResponse)
                .isPresent();

        if (!isProtected) {
            return;
        }

        if (config.placingBlocksBypassOnRegion.contains(type)) {
            return;
        }

        // always cancel to prevent breaking other protection
        // plugins or plugins using PlayerBucketEmptyEvent (eg. special ability blocks)
        event.setCancelled(true);
    }

}

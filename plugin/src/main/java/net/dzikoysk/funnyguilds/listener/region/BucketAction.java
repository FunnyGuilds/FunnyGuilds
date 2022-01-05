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
        ProtectionSystem.isProtected(event.getPlayer(), event.getBlockClicked().getLocation(), true)
                .filter(predicate -> !config.placingBlocksBypassOnRegion.contains(event.getBlockClicked().getType()))
                .peek(result -> event.setCancelled(true))
                .peek(ProtectionSystem::defaultResponse);
    }

    @EventHandler
    public void onEmpty(PlayerBucketEmptyEvent event) {
        ProtectionSystem.isProtected(event.getPlayer(), event.getBlockClicked().getLocation(), true)
                .filter(predicate -> !config.placingBlocksBypassOnRegion.contains(event.getBucket()))
                .peek(result -> event.setCancelled(true))
                .peek(ProtectionSystem::defaultResponse);
    }

}

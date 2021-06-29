package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.system.protection.ProtectionSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

public class BucketAction implements Listener {

    @EventHandler
    public void onFill(PlayerBucketFillEvent event) {
        ProtectionSystem.isProtected(event.getPlayer(), event.getBlockClicked().getLocation(), false)
                .peek(result -> event.setCancelled(true))
                .peek(ProtectionSystem::defaultResponse);
    }

    @EventHandler
    public void onEmpty(PlayerBucketEmptyEvent event) {
        ProtectionSystem.isProtected(event.getPlayer(), event.getBlockClicked().getLocation(), false)
                .peek(result -> event.setCancelled(true))
                .peek(ProtectionSystem::defaultResponse);
    }

}

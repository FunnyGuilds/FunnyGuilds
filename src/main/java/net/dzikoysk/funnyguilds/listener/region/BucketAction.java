package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.system.protection.ProtectionSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

public class BucketAction implements Listener {

    private final ProtectionSystem protectionSystem;

    public BucketAction(FunnyGuilds plugin) {
        this.protectionSystem = plugin.getSystemManager().getProtectionSystem();
    }

    @EventHandler
    public void onFill(PlayerBucketFillEvent event) {
        protectionSystem.isProtected(event.getPlayer(), event.getBlockClicked().getLocation(), false)
                .peek(result -> event.setCancelled(true))
                .peek(protectionSystem::defaultResponse);
    }

    @EventHandler
    public void onEmpty(PlayerBucketEmptyEvent event) {
        protectionSystem.isProtected(event.getPlayer(), event.getBlockClicked().getLocation(), false)
                .peek(result -> event.setCancelled(true))
                .peek(protectionSystem::defaultResponse);
    }

}

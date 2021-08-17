package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.feature.protection.ProtectionSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingPlaceEvent;

public class HangingPlace implements Listener {

    @EventHandler
    public void onPlace(HangingPlaceEvent event) {
        ProtectionSystem.isProtected(event.getPlayer(), event.getEntity().getLocation(), false)
                .peek(result -> event.setCancelled(true))
                .peek(ProtectionSystem::defaultResponse);
    }
    
}

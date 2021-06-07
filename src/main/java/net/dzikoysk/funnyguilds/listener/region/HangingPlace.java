package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.system.protection.ProtectionSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingPlaceEvent;

public class HangingPlace implements Listener {

    private final ProtectionSystem protectionSystem;

    public HangingPlace(FunnyGuilds plugin) {
        this.protectionSystem = plugin.getSystemManager().getProtectionSystem();
    }

    @EventHandler
    public void onPlace(HangingPlaceEvent event) {
        protectionSystem.isProtected(event.getPlayer(), event.getEntity().getLocation(), false)
                .peek(result -> event.setCancelled(true))
                .peek(protectionSystem::defaultResponse);
    }
    
}

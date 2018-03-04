package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.system.protection.ProtectionSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingPlaceEvent;

public class HangingPlace implements Listener {

    @EventHandler
    public void onPlace(HangingPlaceEvent e) {
        if (ProtectionSystem.build(e.getPlayer(), e.getEntity().getLocation())) {
            e.setCancelled(true);
        }
    }
    
}

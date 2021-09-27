package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.feature.protection.ProtectionSystem;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.hanging.HangingPlaceEvent;

public class HangingPlace extends AbstractFunnyListener {

    @EventHandler
    public void onPlace(HangingPlaceEvent event) {
        ProtectionSystem.isProtected(event.getPlayer(), event.getEntity().getLocation(), false)
                .peek(result -> event.setCancelled(true))
                .peek(ProtectionSystem::defaultResponse);
    }

}

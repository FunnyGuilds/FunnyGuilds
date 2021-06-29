package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.system.protection.ProtectionSystem;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPlaceEvent;

public class EntityPlace implements Listener {

    @EventHandler
    public void onSpawn(EntityPlaceEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof EnderCrystal)) {
            return;
        }

        ProtectionSystem.isProtected(event.getPlayer(), entity.getLocation(), true)
                .peek(result -> event.setCancelled(true))
                .peek(ProtectionSystem::defaultResponse);
    }

}

package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.feature.protection.ProtectionSystem;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPlaceEvent;

public class EntityPlace extends AbstractFunnyListener {

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

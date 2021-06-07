package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.system.protection.ProtectionSystem;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPlaceEvent;

public class EntityPlace implements Listener {

    private final ProtectionSystem protectionSystem;

    public EntityPlace(FunnyGuilds plugin) {
        this.protectionSystem = plugin.getSystemManager().getProtectionSystem();
    }

    @EventHandler
    public void onSpawn(EntityPlaceEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof EnderCrystal)) {
            return;
        }

        protectionSystem.isProtected(event.getPlayer(), entity.getLocation(), true)
                .peek(result -> event.setCancelled(true))
                .peek(protectionSystem::defaultResponse);
    }

}

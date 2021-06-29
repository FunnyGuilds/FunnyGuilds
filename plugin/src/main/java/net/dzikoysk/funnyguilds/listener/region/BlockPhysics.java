package net.dzikoysk.funnyguilds.listener.region;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class BlockPhysics implements Listener {

    @EventHandler
    public void onPhysics(BlockPhysicsEvent event) {
        if (GuildHeartProtectionHandler.isGuildHeart(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFall(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.FALLING_BLOCK && GuildHeartProtectionHandler.isGuildHeart(event.getBlock())) {
            event.setCancelled(true);
        }
    }
}

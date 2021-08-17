package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.feature.protection.ProtectionSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;

public class BlockIgnite implements Listener {

    @EventHandler
    public void onIgnite(BlockIgniteEvent event) {
        ProtectionSystem.isProtected(event.getPlayer(), event.getBlock().getLocation(), false)
                .peek(ProtectionSystem::defaultResponse)
                .peek(result -> event.setCancelled(true));
    }

}


package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.feature.protection.ProtectionSystem;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockIgniteEvent;

public class BlockIgnite extends AbstractFunnyListener {

    @EventHandler
    public void onIgnite(BlockIgniteEvent event) {
        ProtectionSystem.isProtected(event.getPlayer(), event.getBlock().getLocation(), false)
                .peek(ProtectionSystem::defaultResponse)
                .peek(result -> event.setCancelled(true));
    }

}


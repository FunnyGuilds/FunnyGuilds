package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.feature.protection.ProtectionSystem;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreak extends AbstractFunnyListener {

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        ProtectionSystem.isProtected(event.getPlayer(), event.getBlock().getLocation(), this.config.regionExplodeBlockBreaking)
                .peek(ProtectionSystem::defaultResponse)
                .peek(result -> event.setCancelled(true));
    }

}

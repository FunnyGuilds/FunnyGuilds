package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.system.protection.ProtectionSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;

public class BlockIgnite implements Listener {

    private final ProtectionSystem protectionSystem;

    public BlockIgnite(FunnyGuilds plugin) {
        this.protectionSystem = plugin.getSystemManager().getProtectionSystem();
    }

    @EventHandler
    public void onIgnite(BlockIgniteEvent event) {
        protectionSystem.isProtected(event.getPlayer(), event.getBlock().getLocation(), false)
                .peek(protectionSystem::defaultResponse)
                .peek(result -> event.setCancelled(true));
    }

}


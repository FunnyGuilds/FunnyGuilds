package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.system.protection.ProtectionSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;

public class BlockIgnite implements Listener {

    @EventHandler
    public void onIgnite(BlockIgniteEvent e) {
        if (ProtectionSystem.isProtected(e.getPlayer(), e.getBlock().getLocation())) {
            e.setCancelled(true);
        }
    }

}


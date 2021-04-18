package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.system.protection.ProtectionSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreak implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        ProtectionSystem.isProtected(event.getPlayer(), event.getBlock().getLocation(), FunnyGuilds.getInstance().getPluginConfiguration().regionExplodeBlockBreaking)
                .peek(ProtectionSystem::defaultResponse)
                .peek(result -> event.setCancelled(true));

    }

}

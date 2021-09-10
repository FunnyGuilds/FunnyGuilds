package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.protection.ProtectionSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreak implements Listener {

    private final FunnyGuilds plugin;

    public BlockBreak(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        ProtectionSystem.isProtected(event.getPlayer(), event.getBlock().getLocation(), plugin.getPluginConfiguration().regionExplodeBlockBreaking)
                .peek(ProtectionSystem::defaultResponse)
                .peek(result -> event.setCancelled(true));

    }

}

package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.system.protection.ProtectionSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreak implements Listener {

    private final ProtectionSystem protectionSystem;

    public BlockBreak(FunnyGuilds plugin) {
        this.protectionSystem = plugin.getSystemManager().getProtectionSystem();
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        protectionSystem.isProtected(event.getPlayer(), event.getBlock().getLocation(), FunnyGuilds.getInstance().getPluginConfiguration().regionExplodeBlockBreaking)
                .peek(protectionSystem::defaultResponse)
                .peek(result -> event.setCancelled(true));

    }

}

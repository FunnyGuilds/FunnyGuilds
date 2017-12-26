package net.dzikoysk.funnyguilds.listener.region;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.system.protection.ProtectionSystem;

public class BlockPlace implements Listener {

    private final PluginConfig config = Settings.getConfig();

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (ProtectionSystem.build(e.getPlayer(), e.getBlock().getLocation(), true)) {
            if (config.buggedBlocks) {
                FunnyGuilds.getInstance().getServer().getScheduler().runTaskLater(FunnyGuilds.getInstance(),
                                () -> e.getBlockReplacedState().update(true), config.buggedBlocksTimer);
                return;
            }
            
            e.setCancelled(true);
        }
    }

}

package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.system.protection.ProtectionSystem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlace implements Listener {

    private final PluginConfig config = Settings.getConfig();

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (ProtectionSystem.build(e.getPlayer(), e.getBlock().getLocation(), true)) {
            e.setCancelled(true);

            if (config.buggedBlocks) {
                Block placedBlock = e.getBlockPlaced();
                Block targetBlock = e.getBlock();

                targetBlock.setType(placedBlock.getType());
                FunnyGuilds.getInstance().getServer().getScheduler().runTaskLater(FunnyGuilds.getInstance(), () -> targetBlock.setType(Material.AIR), 20 * config.buggedBlocksTimer);
            }
        }
    }

}

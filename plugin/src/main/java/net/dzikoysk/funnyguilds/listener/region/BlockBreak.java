package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.feature.protection.GuildProtectionPermission;
import net.dzikoysk.funnyguilds.feature.protection.ProtectionSystem;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreak extends AbstractFunnyListener {

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        // Determine which protection permission to check based on block type
        GuildProtectionPermission protectionPermission = getProtectionPermissionForBlock(event.getBlock().getType());
        
        ProtectionSystem.isProtected(
                        event.getPlayer(),
                        event.getBlock().getLocation(),
                        event,
                        protectionPermission,
                        this.config.regionExplodeBlockBreaking
                )
                .peek(ProtectionSystem::defaultResponse)
                .peek(result -> event.setCancelled(true));
    }

    /**
     * Determines the appropriate protection permission based on block type.
     * Special blocks like Obsidian use more specific permissions.
     */
    private GuildProtectionPermission getProtectionPermissionForBlock(Material type) {
        if (type == Material.OBSIDIAN) {
            return GuildProtectionPermission.OBSIDIAN_BREAK;
        }
        return GuildProtectionPermission.BLOCK_BREAK;
    }

}

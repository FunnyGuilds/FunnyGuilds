package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.feature.protection.GuildProtectionPermission;
import net.dzikoysk.funnyguilds.feature.protection.ProtectionSystem;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreak extends AbstractFunnyListener {

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        var protection = ProtectionSystem.isProtected(
                        event.getPlayer(),
                        event.getBlock().getLocation(),
                        event,
                        GuildProtectionPermission.BLOCK_BREAK,
                        this.config.regionExplodeBlockBreaking
                );
        protection.ifPresent(ProtectionSystem::defaultResponse);
        protection.ifPresent(result -> event.setCancelled(true));
    }

}

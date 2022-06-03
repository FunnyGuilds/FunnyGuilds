package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.feature.protection.ProtectionSystem;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

public class HangingBreak extends AbstractFunnyListener {

    @EventHandler
    public void onBreak(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player)) {
            return;
        }

        ProtectionSystem.isProtected((Player) event.getRemover(), event.getEntity().getLocation(), false)
                .peek(result -> event.setCancelled(true))
                .peek(ProtectionSystem::defaultResponse);
    }

}

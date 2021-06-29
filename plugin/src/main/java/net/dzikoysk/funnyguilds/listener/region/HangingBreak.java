package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.system.protection.ProtectionSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

public class HangingBreak implements Listener {
    
    @EventHandler
    public void onBreak(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getRemover();

        ProtectionSystem.isProtected(player, event.getEntity().getLocation(), false)
                .peek(result -> event.setCancelled(true))
                .peek(ProtectionSystem::defaultResponse);

    }
    
}

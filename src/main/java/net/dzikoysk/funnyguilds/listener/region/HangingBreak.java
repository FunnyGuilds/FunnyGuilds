package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.system.protection.ProtectionSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

public class HangingBreak implements Listener {
    
    @EventHandler
    public void onBreak(HangingBreakByEntityEvent e) {
        if (!(e.getRemover() instanceof Player)) {
            return;
        }
        
        Player p = (Player) e.getRemover();
        
        if (ProtectionSystem.isProtected(p, e.getEntity().getLocation())) {
            e.setCancelled(true);
        }
    }
    
}

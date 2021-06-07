package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.system.protection.ProtectionSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

public class HangingBreak implements Listener {

    private final ProtectionSystem protectionSystem;

    public HangingBreak(FunnyGuilds plugin) {
        this.protectionSystem = plugin.getSystemManager().getProtectionSystem();
    }
    
    @EventHandler
    public void onBreak(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getRemover();

        protectionSystem.isProtected(player, event.getEntity().getLocation(), false)
                .peek(result -> event.setCancelled(true))
                .peek(protectionSystem::defaultResponse);

    }
    
}

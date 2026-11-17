package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.feature.protection.GuildProtectionPermission;
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

        var protection = ProtectionSystem.isProtected(
                        (Player) event.getRemover(),
                        event.getEntity().getLocation(),
                        event,
                        GuildProtectionPermission.HANGING_BREAK,
                        false
                );
        protection.ifPresent(result -> event.setCancelled(true));
        protection.ifPresent(ProtectionSystem::defaultResponse);
    }

}

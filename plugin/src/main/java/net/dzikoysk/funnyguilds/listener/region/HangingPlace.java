package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.feature.protection.GuildProtectionPermission;
import net.dzikoysk.funnyguilds.feature.protection.ProtectionSystem;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.hanging.HangingPlaceEvent;

public class HangingPlace extends AbstractFunnyListener {

    @EventHandler
    public void onPlace(HangingPlaceEvent event) {
        var protection = ProtectionSystem.isProtected(
                        event.getPlayer(),
                        event.getEntity().getLocation(),
                        event,
                        GuildProtectionPermission.HANGING_PLACE,
                        false
                );
        protection.ifPresent(result -> event.setCancelled(true));
        protection.ifPresent(ProtectionSystem::defaultResponse);
    }

}

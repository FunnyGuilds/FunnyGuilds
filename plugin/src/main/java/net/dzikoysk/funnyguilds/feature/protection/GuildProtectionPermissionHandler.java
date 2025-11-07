package net.dzikoysk.funnyguilds.feature.protection;

import net.dzikoysk.funnyguilds.guild.permission.event.GuildPermissionProtectionCheckEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class GuildProtectionPermissionHandler implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGuildProtectionPermissionCheck(GuildPermissionProtectionCheckEvent event) {
        if (!(event.getPermission() instanceof GuildProtectionPermission)) {
            return;
        }
        event.getDoer().peekIfNot(event.getGuild()::isMember, user -> event.setCancelled(true));
    }

}

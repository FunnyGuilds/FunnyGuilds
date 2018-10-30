package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.basic.user.User;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawn implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(final PlayerRespawnEvent event) {
        final User user = User.get(event.getPlayer());
        if (!user.hasGuild()) {
            return;
        }
        
        final Location home = user.getGuild().getHome();
        if (home == null) {
            return;
        }
        
        event.setRespawnLocation(home);
    }

}

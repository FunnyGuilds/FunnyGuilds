package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserCache;
import net.dzikoysk.funnyguilds.element.tablist.AbstractTablist;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        quit(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        quit(event.getPlayer());
    }

    private void quit(Player player) {
        User user = User.get(player);
        user.removeFromCache();

        UserCache cache = user.getCache();
        cache.setPrefix(null);
        cache.setScoreboard(null);
        cache.setDummy(null);
        cache.clearDamage();

        AbstractTablist.removeTablist(player);
        user.getBossBar().removeNotification();
    }

}

package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        handleQuit(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        handleQuit(event.getPlayer());
    }

    private void handleQuit(Player player) {
        User user = User.get(player);

        if (user != null) {
            UserCache cache = user.getCache();

            if (cache.isInCombat()) {
                user.getRank().addLogout();
            }

            cache.setIndividualPrefix(null);
            cache.setScoreboard(null);
            cache.setDummy(null);
            cache.setPlayerList(null);
            cache.clearDamage();

            user.getBossBar().removeNotification();
        }
    }

}

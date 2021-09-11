package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import panda.std.Option;

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
        Option<User> userOption = UserManager.getInstance().getUser(player);

        if (userOption.isEmpty()) {
            return;
        }

        User user = userOption.get();
        UserCache cache = user.getCache();

        if (cache.isInCombat()) {
            user.getRank().addLogouts(1);
        }

        cache.setIndividualPrefix(null);
        cache.setScoreboard(null);
        cache.setDummy(null);
        cache.setPlayerList(null);
        cache.clearDamage();

        user.getBossBar().removeNotification();
    }

}

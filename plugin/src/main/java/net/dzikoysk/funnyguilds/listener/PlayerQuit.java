package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.event.FunnyEvent;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.rank.LogoutsChangeEvent;
import net.dzikoysk.funnyguilds.user.UserCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit extends AbstractFunnyListener {

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        this.handleQuit(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.handleQuit(event.getPlayer());
    }

    private void handleQuit(Player player) {
        this.userManager.findByPlayer(player).peek(user -> {
            UserCache cache = user.getCache();

            if (cache.getDamageCache().isInCombat()) {
                LogoutsChangeEvent logoutsChangeEvent = new LogoutsChangeEvent(FunnyEvent.EventCause.USER, user, user, 1);
                if (SimpleEventHandler.handle(logoutsChangeEvent)) {
                    user.getRank().updateLogouts(currentValue -> currentValue + logoutsChangeEvent.getLogoutsChange());
                }
            }

            cache.setIndividualPrefix(null);
            cache.setScoreboard(null);
            cache.setDummy(null);
            cache.setPlayerList(null);
            cache.getDamageCache().clear();

            this.bossBarService.getBossBarProvider(this.funnyServer, user).removeNotification();
        });
    }

}

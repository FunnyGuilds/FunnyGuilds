package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.element.NotificationBar;
import net.dzikoysk.funnyguilds.element.tablist.AbstractTablist;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        User user = User.get(player);
        
        NotificationBar.remove(player);
        
        user.setIndividualPrefix(null);
        user.setScoreboard(null);
        user.setDummy(null);
        user.removeFromCache();
        user.clearDamage();
        
        AbstractTablist.removeTablist(player);
    }

}

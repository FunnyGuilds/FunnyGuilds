package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.util.element.NotificationBar;
import net.dzikoysk.funnyguilds.util.element.tablist.AbstractTablist;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

public class PlayerKick implements Listener {

    @EventHandler
    public void onQuit(PlayerKickEvent event) {
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

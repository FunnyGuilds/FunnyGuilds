package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.system.protection.ProtectionSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

public class BucketActionListener implements Listener {

    @EventHandler
    public void onFill(PlayerBucketFillEvent e) {
        if (ProtectionSystem.build(e.getPlayer(), e.getBlockClicked().getLocation()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onEmpty(PlayerBucketEmptyEvent e) {
        if (ProtectionSystem.build(e.getPlayer(), e.getBlockClicked().getLocation()))
            e.setCancelled(true);
    }

}

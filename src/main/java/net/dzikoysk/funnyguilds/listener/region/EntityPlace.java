package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.system.protection.ProtectionSystem;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPlaceEvent;

public class EntityPlace implements Listener {

    @EventHandler
    public void onSpawn(EntityPlaceEvent event) {
        Entity entity = event.getEntity();
        Player player = event.getPlayer();

        if (! (entity instanceof EnderCrystal)) {
            return;
        }

        if (ProtectionSystem.isProtected(player, entity.getLocation(), true)) {
            event.setCancelled(true);
            player.sendMessage(FunnyGuilds.getInstance().getMessageConfiguration().regionOther);
        }
    }
}

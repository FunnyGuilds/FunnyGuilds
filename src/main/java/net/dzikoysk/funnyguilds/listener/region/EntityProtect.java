package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityProtect implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        Entity entity = event.getEntity();

        if (!config.explodeShouldAffectOnlyGuild) {
            return;
        }

        if (!(entity instanceof Mob)) {
            return;
        }

        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION && event.getCause() != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
            return;
        }

        event.setCancelled(true);
    }

}

package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashSet;
import java.util.Set;

public class EntityProtect implements Listener {

    private static final Set<EntityType> IS_NOT_MOB = new HashSet<>();

    static {
        IS_NOT_MOB.add(EntityType.ARMOR_STAND);
        IS_NOT_MOB.add(EntityType.PLAYER);
    }

    private final FunnyGuilds plugin;

    public EntityProtect(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        PluginConfiguration config = plugin.getPluginConfiguration();

        Entity entity = event.getEntity();

        if (!config.explodeShouldAffectOnlyGuild) {
            return;
        }

        if (!(entity instanceof LivingEntity && !IS_NOT_MOB.contains(entity.getType()))) {
            return;
        }

        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION && event.getCause() != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
            return;
        }

        event.setCancelled(true);
    }

}

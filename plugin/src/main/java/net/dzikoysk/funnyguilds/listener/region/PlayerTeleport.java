package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerTeleport extends AbstractFunnyListener {

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        this.userManager.findByUuid(event.getPlayer().getUniqueId())
                .filterNot(user -> user.hasPermission("funnyguilds.admin"))
                .peek(user -> this.regionManager.findRegionAtLocation(event.getTo())
                        .map(Region::getGuild)
                        .filterNot(guild -> guild.getMembers().contains(user))
                        .filterNot(guild -> this.isTeleportationToRegionAllowed(guild, user.getGuild().orNull()))
                        .peek(guild -> {
                            user.sendMessage(messages.regionTeleport);
                            event.setCancelled(true);
                        }));
    }

    private boolean isTeleportationToRegionAllowed(Guild guild, Guild userGuild) {
        return !config.blockTeleportOnRegion.neutral &&
                this.isTeleportationOnAllyRegionAllowed(guild, userGuild) &&
                this.isTeleportationOnEnemyRegionAllowed(guild, userGuild);
    }

    private boolean isTeleportationOnEnemyRegionAllowed(Guild guild, Guild userGuild) {
        return !config.blockTeleportOnRegion.enemy ||
                !guild.getEnemies().contains(userGuild);
    }

    private boolean isTeleportationOnAllyRegionAllowed(Guild guild, Guild userGuild) {
        return !config.blockTeleportOnRegion.ally ||
                !guild.getAllies().contains(userGuild);
    }

}

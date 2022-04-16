package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerTeleport extends AbstractFunnyListener {

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        this.userManager.findByUuid(event.getPlayer().getUniqueId())
                .filterNot(user -> user.hasPermission("funnyguilds.admin"))
                .filterNot(user -> this.isTeleportationToRegionAllowed(event.getTo(), user))
                .peek(user -> {
                    user.sendMessage(messages.regionTeleport);
                    event.setCancelled(true);
                });
    }

    private boolean isTeleportationToRegionAllowed(Location to, User user) {
        return this.regionManager.findRegionAtLocation(to)
                .map(Region::getGuild)
                .filter(guild -> guild.getMembers().contains(user) ||
                        this.isTeleportationToRegionAllowed(guild, user.getGuild().orNull()))
                .isPresent();
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

package net.dzikoysk.funnyguilds.listener.region;

import java.util.Optional;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.jetbrains.annotations.Nullable;

public class PlayerTeleport extends AbstractFunnyListener {

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        this.userManager.findByUuid(player.getUniqueId())
                .filter(user -> event.getCause() == TeleportCause.COMMAND || event.getCause() == TeleportCause.PLUGIN)
                .filter(user -> !user.hasPermission("funnyguilds.admin.teleport"))
                .filter(user -> !this.isTeleportationToRegionAllowed(event.getTo(), user))
                .ifPresent(user -> {
                    this.messageService.getMessage(config -> config.regionTeleport)
                            .receiver(player)
                            .send();
                    event.setCancelled(true);
                });
    }

    private boolean isTeleportationToRegionAllowed(Location to, User user) {
        Optional<Region> regionOption = this.regionManager.findRegionAtLocation(to);
        if (regionOption.isEmpty()) {
            return true;
        }

        Guild guild = regionOption.orElseThrow().getGuild();
        return guild.isMember(user) || this.isTeleportationToRegionAllowed(guild, user.getGuild().orElse(null));
    }

    private boolean isTeleportationToRegionAllowed(Guild guild, @Nullable Guild userGuild) {
        if (userGuild == null) {
            return true;
        }

        return this.isTeleportationOnNeutralRegionAllowed(guild, userGuild) &&
                this.isTeleportationOnEnemyRegionAllowed(guild, userGuild) &&
                this.isTeleportationOnAllyRegionAllowed(guild, userGuild);
    }

    private boolean isTeleportationOnNeutralRegionAllowed(Guild guild, @Nullable Guild userGuild) {
        return !this.config.blockTeleportOnRegion.neutral || !guild.isNeutral(userGuild);
    }

    private boolean isTeleportationOnEnemyRegionAllowed(Guild guild, @Nullable Guild userGuild) {
        return !this.config.blockTeleportOnRegion.enemy || !guild.isAtWar(userGuild);
    }

    private boolean isTeleportationOnAllyRegionAllowed(Guild guild, @Nullable Guild userGuild) {
        return !this.config.blockTeleportOnRegion.ally || !guild.isAlly(userGuild);
    }

}

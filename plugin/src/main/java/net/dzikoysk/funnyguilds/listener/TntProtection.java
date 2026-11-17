package net.dzikoysk.funnyguilds.listener;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.UUID;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.shared.Cooldown;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityExplodeEvent;

public class TntProtection extends AbstractFunnyListener {

    private final Cooldown<UUID> informationMessageCooldowns = new Cooldown<>();

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent event) {
        if (this.config.tntProtection.time.exemptEntities.contains(event.getEntityType())) {
            return;
        }

        if (this.tntCanExplode()) {
            return;
        }

        if (this.hasGuildTntBypass(event)) {
            event.blockList().removeIf(block -> this.regionManager.findRegionAtLocation(block.getLocation())
                    .map(Region::getGuild)
                    .filter(guild -> !guild.hasTntProtectionBypass())
                    .isPresent());
            return;
        }

        if (this.config.tntProtection.time.enabledGlobal) {
            event.setCancelled(true);
            return;
        }

        if (this.config.tntProtection.time.enabled) {
            if (this.regionManager.findRegionAtLocation(event.getLocation()).isPresent()) {
                event.setCancelled(true);
                return;
            }

            event.blockList().removeIf(block -> this.regionManager.findRegionAtLocation(block.getLocation()).isPresent());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockBuildingOnGuildRegionOnExplosion(EntityExplodeEvent event) {
        Set<Player> players = this.regionManager.findRegionAtLocation(event.getLocation())
                .map(Region::getGuild)
                .filter(guild -> !(this.config.war.tntProtection && !this.config.regionExplodeBlockProtected && !guild.canBeAttacked()))
                .filter(guild -> this.config.regionExplodeBlockTntDisabled || this.tntCanExplode())
                .filter(guild -> !(event.getEntity() instanceof TNTPrimed && guild.hasTntProtectionBypass()))
                .filter(guild -> !this.config.regionExplodeExcludeEntities.contains(event.getEntityType()))
                .map(guild -> {
                    guild.setBuild(Instant.now().plus(this.config.regionExplode));
                    return guild.getMembers().stream();
                })
                .orElse(Stream.empty())
                .filter(user -> !this.informationMessageCooldowns.cooldown(user.getUUID(), this.config.infoPlayerCooldown))
                .flatMap(user -> this.funnyServer.getPlayer(user).stream())
                .collect(Collectors.toSet());

        this.messageService.getMessage( config -> config.regionExplode)
                .with("{TIME}", this.config.regionExplode.getSeconds())
                .receivers(players)
                .send();
    }

    private boolean hasGuildTntBypass(EntityExplodeEvent event) {
        return event.getEntity() instanceof TNTPrimed
                && this.regionManager.findRegionAtLocation(event.getLocation())
                .map(Region::getGuild)
                .filter(Guild::hasTntProtectionBypass)
                .isPresent();
    }

    private boolean tntCanExplode() {
        if (!this.config.tntProtection.time.enabled && !this.config.tntProtection.time.enabledGlobal) {
            return true;
        }

        LocalTime now = LocalTime.now();
        LocalTime start = this.config.tntProtection.time.startTime.getTime();
        LocalTime end = this.config.tntProtection.time.endTime.getTime();

        boolean isWithinTimeframe = this.config.tntProtection.time.passingMidnight
                ? now.isAfter(start) || now.isBefore(end)
                : now.isAfter(start) && now.isBefore(end);

        return !isWithinTimeframe;
    }

}

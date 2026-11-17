package net.dzikoysk.funnyguilds.feature.protection;

import dev.peri.yetanothermessageslibrary.message.Sendable;
import java.time.Duration;
import java.util.Optional;
import java.time.Instant;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.shared.Optionals;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.sections.HeartConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.permission.GuildPermission;
import net.dzikoysk.funnyguilds.shared.bukkit.FunnyBox;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import panda.std.Triple;

public final class ProtectionSystem {

    private ProtectionSystem() {
    }

    public static Optional<Triple<Player, Guild, ProtectionType>> isProtected(Player player, Location location, Event triggerEvent, GuildPermission<Boolean> permission, boolean includeBuildLock) {
        if (player == null || location == null) {
            return Optional.empty();
        }

        FunnyGuilds plugin = FunnyGuilds.getInstance();

        Optional<Region> regionOption = plugin.getRegionManager().findRegionAtLocation(location);
        if (regionOption.isEmpty()) {
            return Optional.empty();
        }
        Region region = regionOption.orElseThrow();
        Guild guild = region.getGuild();

        PluginConfiguration config = plugin.getPluginConfiguration();
        HeartConfiguration heartConfig = config.heart;
        if (region.getHeart().filter(heart -> heart.equals(location)).isPresent()) {
            Material heartMaterial = heartConfig.createMaterial;
            return Optionals.when(heartMaterial != null && heartMaterial != Material.AIR, () -> Triple.of(player, guild, ProtectionType.HEART));
        }

        if (player.hasPermission("funnyguilds.admin.build")) {
            return Optional.empty();
        }
        
        User user = plugin.getUserManager()
                .findByUuid(player.getUniqueId())
                .orElseThrow(() -> new IllegalStateException("User not found for player " + player.getName()));
        
        boolean canPerformAction = plugin.getGuildPermissionChecker().handleProtectionPermission(
                guild,
                user,
                permission,
                triggerEvent
        );
        if (!canPerformAction) {
            return Optional.of(Triple.of(player, guild, ProtectionType.UNAUTHORIZED));
        }

        if (includeBuildLock && !guild.canBuild()) {
            return Optional.of(Triple.of(player, guild, ProtectionType.LOCKED));
        }

        if (isGuildHeartProtectedRegion(location)) {
            return Optional.of(Triple.of(player, guild, ProtectionType.HEART_INTERACTION));
        }

        return Optional.empty();
    }

    public static boolean isGuildHeartProtectedRegion(Location location) {
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        HeartConfiguration heartConfig = plugin.getPluginConfiguration().heart;
        if (!heartConfig.interactionProtection.enabled) {
            return false;
        }

        Optional<Region> regionOption = plugin.getRegionManager().findRegionAtLocation(location);
        if (regionOption.isEmpty()) {
            return false;
        }
        Region region = regionOption.orElseThrow();
        Guild guild = region.getGuild();

        return guild.getEnderCrystal()
                .map(Location::getBlock)
                .map(FunnyBox::of)
                .map(box -> box.expandDirectional(heartConfig.interactionProtection.firstCorner, heartConfig.interactionProtection.secondCorner))
                .map(box -> box.contains(location))
                .orElse(false);
    }

    public static void defaultResponse(Triple<Player, Guild, ProtectionType> result) {
        Player player = result.getFirst();
        ProtectionType protectionType = result.getThird();

        Function<MessageConfiguration, Sendable> messageSupplier;
        switch (protectionType) {
            case UNAUTHORIZED:
                // Do nothing, message is sent in GuildProtectionPermissionHandler
                return;
            case HEART:
                messageSupplier = config -> config.regionCenter;
                break;
            case HEART_INTERACTION:
                messageSupplier = config -> config.regionInteract;
                break;
            case LOCKED:
                ProtectionSystem.sendRegionExplodeMessage(player, result.getSecond());
                return;
            default:
                messageSupplier = config -> config.regionOther;
                break;
        }
        FunnyGuilds.getInstance().getMessageService().getMessage(messageSupplier)
                .receiver(player)
                .send();
    }

    private static void sendRegionExplodeMessage(Player player, Guild guild) {
        guild.getBuild().ifPresent(build -> {
            Duration time = Duration.between(Instant.now(), build);
            FunnyGuilds.getInstance().getMessageService().getMessage(config -> config.regionExplodeInteract)
                    .with("{TIME}", time.getSeconds())
                    .receiver(player)
                    .send();
        });
    }

    public enum ProtectionType {
        
        UNAUTHORIZED,
        LOCKED,
        HEART,
        HEART_INTERACTION

    }

}

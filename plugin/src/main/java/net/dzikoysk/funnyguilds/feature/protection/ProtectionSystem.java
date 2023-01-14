package net.dzikoysk.funnyguilds.feature.protection;

import java.time.Duration;
import java.time.Instant;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.sections.HeartConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.FunnyBox;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import panda.std.Option;
import panda.std.Pair;
import panda.std.Triple;

public final class ProtectionSystem {

    private ProtectionSystem() {
    }

    public static Option<Triple<Player, Guild, ProtectionType>> isProtected(Player player, Location location, boolean includeBuildLock) {
        if (player == null || location == null) {
            return Option.none();
        }

        FunnyGuilds plugin = FunnyGuilds.getInstance();

        Option<Region> regionOption = plugin.getRegionManager().findRegionAtLocation(location);
        if (regionOption.isEmpty()) {
            return Option.none();
        }
        Region region = regionOption.get();
        Guild guild = region.getGuild();

        PluginConfiguration config = plugin.getPluginConfiguration();
        HeartConfiguration heartConfig = config.heart;
        if (region.getHeart().contentEquals(location)) {
            Pair<Material, Byte> heartMaterial = heartConfig.createMaterial;
            return Option.when(heartMaterial != null && heartMaterial.getFirst() != Material.AIR, Triple.of(player, guild, ProtectionType.HEART));
        }

        if (player.hasPermission("funnyguilds.admin.build")) {
            return Option.none();
        }

        Option<User> userOption = plugin.getUserManager().findByUuid(player.getUniqueId());
        if (!userOption.is(guild::isMember)) {
            return Option.of(Triple.of(player, guild, ProtectionType.UNAUTHORIZED));
        }

        if (includeBuildLock && !guild.canBuild()) {
            return Option.of(Triple.of(player, guild, ProtectionType.LOCKED));
        }

        if (heartConfig.interactionProtection.enabled && isGuildHeartProtectedRegion(location)) {
            return Option.of(Triple.of(player, guild, ProtectionType.HEART_INTERACTION));
        }

        return Option.none();
    }

    public static boolean isGuildHeartProtectedRegion(Location location) {
        FunnyGuilds plugin = FunnyGuilds.getInstance();

        Option<Region> regionOption = plugin.getRegionManager().findRegionAtLocation(location);
        if (regionOption.isEmpty()) {
            return false;
        }
        Region region = regionOption.get();
        Guild guild = region.getGuild();

        HeartConfiguration heartConfig = plugin.getPluginConfiguration().heart;
        return guild.getEnderCrystal()
                .map(Location::getBlock)
                .map(FunnyBox::of)
                .map(box -> box.expandDirectional(heartConfig.interactionProtection.firstCorner, heartConfig.interactionProtection.secondCorner))
                .map(box -> box.contains(location))
                .orElseGet(false);
    }

    public static void defaultResponse(Triple<Player, Guild, ProtectionType> result) {
        Player player = result.getFirst();
        ProtectionType protectionType = result.getThird();
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();

        switch (protectionType) {
            case UNAUTHORIZED:
                ChatUtils.sendMessage(player, messages.regionUnauthorized);
                break;
            case HEART:
                ChatUtils.sendMessage(player, messages.regionCenter);
                break;
            case HEART_INTERACTION:
                ChatUtils.sendMessage(player, messages.regionInteract);
                break;
            case LOCKED:
                ProtectionSystem.sendRegionExplodeMessage(player, result.getSecond());
                break;
            default:
                ChatUtils.sendMessage(player, messages.regionOther);
                break;
        }
    }

    private static void sendRegionExplodeMessage(Player player, Guild guild) {
        guild.getBuild().peek(build -> {
            Duration time = Duration.between(Instant.now(), build);
            MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
            ChatUtils.sendMessage(player, FunnyFormatter.format(messages.regionExplodeInteract, "{TIME}", time.getSeconds()));
        });
    }

    public enum ProtectionType {

        UNAUTHORIZED,
        LOCKED,
        HEART,
        HEART_INTERACTION

    }

}

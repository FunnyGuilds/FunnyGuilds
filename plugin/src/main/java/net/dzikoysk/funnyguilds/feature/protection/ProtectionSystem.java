package net.dzikoysk.funnyguilds.feature.protection;

import java.util.concurrent.TimeUnit;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import panda.std.Option;

public final class ProtectionSystem {

    public enum ProtectionType {
        UNAUTHORIZED,
        LOCKED,
        HEART
    }

    public static Option<Triple<Player, Guild, ProtectionType>> isProtected(Player player, Location location, boolean includeBuildLock) {
        if (player == null || location == null) {
            return Option.none();
        }

        if (player.hasPermission("funnyguilds.admin.build")) {
            return Option.none();
        }

        Option<Region> regionOption = FunnyGuilds.getInstance().getRegionManager().findRegionAtLocation(location);
        if (regionOption.isEmpty()) {
            return Option.none();
        }
        Region region = regionOption.get();

        if (!region.hasGuild() || region.getGuild().getOrNull() == null) {
            return Option.none();
        }
        Guild guild = region.getGuild().get();

        User user = UserUtils.get(player.getUniqueId());
        if (!guild.getMembers().contains(user)) {
            return Option.of(Triple.of(player, guild, ProtectionType.UNAUTHORIZED));
        }

        if (includeBuildLock && !guild.canBuild()) {
            return Option.of(Triple.of(player, guild, ProtectionType.LOCKED));
        }

        if (location.equals(region.getHeartOption().getOrNull())) {
            PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
            Pair<Material, Byte> heartMaterial = config.heart.createMaterial;
            return Option.when(heartMaterial != null && heartMaterial.getLeft() != Material.AIR, Triple.of(player, guild, ProtectionType.HEART));
        }

        return Option.none();
    }

    public static void defaultResponse(Triple<Player, Guild, ProtectionType> result) {
        if (result.getRight() == ProtectionType.LOCKED) {
            ProtectionSystem.sendRegionExplodeMessage(result.getLeft(), result.getMiddle());
        }
        else {
            result.getLeft().sendMessage(FunnyGuilds.getInstance().getMessageConfiguration().regionOther);
        }
    }

    private static void sendRegionExplodeMessage(Player player, Guild guild) {
        player.sendMessage(FunnyGuilds.getInstance().getMessageConfiguration().regionExplodeInteract
                .replace("{TIME}", Long.toString(TimeUnit.MILLISECONDS.toSeconds(guild.getBuild() - System.currentTimeMillis())))
        );
    }

}

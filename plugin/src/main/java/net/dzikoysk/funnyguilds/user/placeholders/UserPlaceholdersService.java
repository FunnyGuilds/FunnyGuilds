package net.dzikoysk.funnyguilds.user.placeholders;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.hooks.HookManager;
import net.dzikoysk.funnyguilds.feature.hooks.vault.VaultHook;
import net.dzikoysk.funnyguilds.feature.placeholders.AbstractPlaceholdersService;
import net.dzikoysk.funnyguilds.rank.DefaultTops;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import panda.std.stream.PandaStream;
import panda.utilities.text.Joiner;

public class UserPlaceholdersService extends AbstractPlaceholdersService<User, UserPlaceholders> {

    public static UserPlaceholders createUserPlaceholders(FunnyGuilds plugin) {
        PluginConfiguration config = plugin.getPluginConfiguration();
        return new UserPlaceholders()
                .property("name", User::getName)
                .property("player", User::getName)
                .property("ping", User::getPing)
                .property("ping-format", user -> FunnyFormatter.format(NumberRange.inRangeToString(user.getPing(),
                        config.pingFormat), "{PING}", user.getPing()))
                .property("position", (user, rank) -> rank.getPosition(DefaultTops.USER_POINTS_TOP))
                .property("points", (user, rank) -> rank.getPoints())
                .property("points-format", (user, rank) -> FunnyFormatter.format(NumberRange.inRangeToString(rank.getPoints(),
                        config.pointsFormat), "{POINTS}", rank.getPoints()))
                .property("kills", (user, rank) -> rank.getKills())
                .property("deaths", (user, rank) -> rank.getDeaths())
                .property("kdr", (user, rank) -> String.format(Locale.US, "%.2f", rank.getKDR()))
                .property("assists", (user, rank) -> rank.getAssists())
                .property("logouts", (user, rank) -> rank.getLogouts());
    }

    public static UserPlaceholders createPlayerPlaceholders(FunnyGuilds plugin) {
        String wgRegionNoValue = plugin.getMessageConfiguration().wgRegionNoValue;
        return new UserPlaceholders()
                .playerOptionProperty("world", playerOption -> playerOption
                        .map(Player::getWorld)
                        .map(World::getName)
                        .orElseGet(""))
                .playerOptionProperty("online", playerOption -> playerOption
                        .map(player -> PandaStream.of(Bukkit.getOnlinePlayers()).filter(player::canSee).count())
                        .orElseGet(0L))
                .playerProperty("wg-region", player -> {
                    List<String> regionNames = getWorldGuardRegionNames(player);
                    return !regionNames.isEmpty() ? regionNames.get(0) : wgRegionNoValue;
                })
                .playerProperty("wg-regions", player -> {
                    List<String> regionNames = getWorldGuardRegionNames(player);
                    return !regionNames.isEmpty() ? Joiner.on(", ").join(regionNames) : wgRegionNoValue;
                })
                .playerOptionProperty("vault-money", playerOption -> playerOption
                        .filter(player -> HookManager.VAULT.isPresent() && VaultHook.isEconomyHooked())
                        .map(VaultHook::accountBalance)
                        .map(value -> String.format(Locale.US, "%.2f", value))
                        .orElseGet(""));
    }

    private static List<String> getWorldGuardRegionNames(Player player) {
        if (player == null) {
            return Collections.emptyList();
        }

        Location location = player.getLocation();
        List<String> regionNames = HookManager.WORLD_GUARD.map(worldGuard -> worldGuard.getRegionNames(location)).orNull();

        if (regionNames != null && !regionNames.isEmpty()) {
            return regionNames;
        }

        return Collections.emptyList();
    }

}

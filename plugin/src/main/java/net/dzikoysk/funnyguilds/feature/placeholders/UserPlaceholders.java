package net.dzikoysk.funnyguilds.feature.placeholders;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.hooks.HookManager;
import net.dzikoysk.funnyguilds.feature.hooks.vault.VaultHook;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.MonoResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.PairResolver;
import net.dzikoysk.funnyguilds.rank.DefaultTops;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserRank;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import panda.std.Option;
import panda.utilities.text.Joiner;

public class UserPlaceholders extends Placeholders<User, UserPlaceholders> {

    private static UserPlaceholders USER;
    private static UserPlaceholders PLAYER;

    public UserPlaceholders property(String name, PairResolver<User, UserRank> resolver) {
        return this.property(name, user -> resolver.resolve(user, user.getRank()));
    }

    public UserPlaceholders playerProperty(String name, MonoResolver<Player> resolver) {
        return this.property(name, user -> resolver.resolve(Bukkit.getPlayer(user.getUUID())));
    }

    public UserPlaceholders playerOptionProperty(String name, MonoResolver<Option<Player>> resolver) {
        return this.playerProperty(name, player -> resolver.resolve(Option.of(player)));
    }

    @Override
    public UserPlaceholders create() {
        return new UserPlaceholders();
    }

    public static UserPlaceholders getOrInstallUserPlaceholders(FunnyGuilds plugin) {
        if (USER == null) {
            PluginConfiguration config = plugin.getPluginConfiguration();
            USER = new UserPlaceholders()
                    .property("name", User::getName)
                    .property("player", User::getName)
                    .property("ping", User::getPing)
                    .property("ping-format", user ->
                            NumberRange.inRangeToString(user.getPing(), config.pingFormat)
                                    .replace("{PING}", String.valueOf(user.getPing())))
                    .property("position", (user, rank) -> rank.getPosition(DefaultTops.USER_POINTS_TOP))
                    .property("points", (user, rank) -> rank.getPoints())
                    .property("points-format", (user, rank) ->
                            NumberRange.inRangeToString(rank.getPoints(), config.pointsFormat)
                                    .replace("{POINTS}", String.valueOf(rank.getPoints())))
                    .property("kills", (user, rank) -> rank.getKills())
                    .property("deaths", (user, rank) -> rank.getDeaths())
                    .property("kdr", (user, rank) -> String.format(Locale.US, "%.2f", rank.getKDR()))
                    .property("assists", (user, rank) -> rank.getAssists())
                    .property("logouts", (user, rank) -> rank.getLogouts());
        }
        return USER;
    }

    public static UserPlaceholders getOrInstallPlayerPlaceholders(FunnyGuilds plugin) {
        if (PLAYER == null) {
            PLAYER = new UserPlaceholders()
                    .playerOptionProperty("world", playerOption -> playerOption
                            .map(Player::getWorld)
                            .map(World::getName)
                            .orElseGet(""))
                    .playerOptionProperty("online", playerOption -> playerOption
                            .map(player -> Bukkit.getOnlinePlayers().stream().filter(player::canSee).count())
                            .orElseGet(0L));

            if (HookManager.WORLD_GUARD.isPresent()) {
                String wgRegionNoValue = plugin.getMessageConfiguration().wgRegionNoValue;

                PLAYER.playerProperty("wg-region", player -> {
                    List<String> regionNames = getWorldGuardRegionNames(player);
                    return !regionNames.isEmpty() ? regionNames.get(0) : wgRegionNoValue;
                });

                PLAYER.playerProperty("wg-regions", player -> {
                    List<String> regionNames = getWorldGuardRegionNames(player);
                    return !regionNames.isEmpty() ? Joiner.on(", ").join(regionNames) : wgRegionNoValue;
                });
            }

            if (HookManager.VAULT.isPresent() && VaultHook.isEconomyHooked()) {
                PLAYER.playerOptionProperty("vault-money", playerOption -> playerOption
                        .map(VaultHook::accountBalance)
                        .map(value -> Double.toString(value))
                        .orElseGet(""));
            }
        }

        return PLAYER;
    }

    private static List<String> getWorldGuardRegionNames(Player player) {
        if (player == null) {
            return Collections.emptyList();
        }

        Location location = player.getLocation();
        List<String> regionNames = HookManager.WORLD_GUARD.map(worldGuard -> worldGuard.getRegionNames(location))
                .orNull();

        if (regionNames != null && !regionNames.isEmpty()) {
            return regionNames;
        }

        return Collections.emptyList();
    }

}

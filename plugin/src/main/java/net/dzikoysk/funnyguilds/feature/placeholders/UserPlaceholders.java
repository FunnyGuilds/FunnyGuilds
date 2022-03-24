package net.dzikoysk.funnyguilds.feature.placeholders;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.hooks.HookManager;
import net.dzikoysk.funnyguilds.feature.hooks.vault.VaultHook;
import net.dzikoysk.funnyguilds.feature.placeholders.placeholder.UserPlaceholder;
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

public class UserPlaceholders extends Placeholders<User, UserPlaceholder> {

    public static final UserPlaceholders USER;
    public static final UserPlaceholders PLAYER;

    static {
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        PluginConfiguration config = plugin.getPluginConfiguration();

        USER = new UserPlaceholders()
                .userProperty(Arrays.asList("player", "name"), User::getName)
                .userProperty("ping", User::getPing)
                .userProperty("ping-format", user ->
                        NumberRange.inRangeToString(user.getPing(), config.pingFormat)
                                .replace("{PING}", String.valueOf(user.getPing())))
                .userProperty("position", (user, rank) -> rank.getPosition(DefaultTops.USER_POINTS_TOP))
                .userProperty("points", (user, rank) -> rank.getPoints())
                .userProperty("points-format", (user, rank) ->
                        NumberRange.inRangeToString(rank.getPoints(), config.pointsFormat)
                                .replace("{POINTS}", String.valueOf(rank.getPoints())))
                .userProperty("kills", (user, rank) -> rank.getKills())
                .userProperty("deaths", (user, rank) -> rank.getDeaths())
                .userProperty("kdr", (user, rank) -> String.format(Locale.US, "%.2f", rank.getKDR()))
                .userProperty("assists", (user, rank) -> rank.getAssists())
                .userProperty("logouts", (user, rank) -> rank.getLogouts());

        PLAYER = new UserPlaceholders()
                .playerOptionProperty("world", playerOption -> playerOption
                        .map(Player::getWorld)
                        .map(World::getName)
                        .orElseGet(""))
                .playerOptionProperty("online", playerOption -> playerOption
                        .map(player -> Bukkit.getOnlinePlayers().stream().filter(player::canSee).count())
                        .orElseGet(0L));

        if (HookManager.WORLD_GUARD.isPresent()) {
            String wgRegionNoValue = FunnyGuilds.getInstance().getMessageConfiguration().wgRegionNoValue;

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

    protected UserPlaceholders userProperty(String name, MonoResolver<User> resolver) {
        this.property(name, new UserPlaceholder(resolver));
        return this;
    }

    protected UserPlaceholders userProperty(Collection<String> names, MonoResolver<User> resolver) {
        names.forEach(name -> this.userProperty(name, resolver));
        return this;
    }

    protected UserPlaceholders userProperty(String name, PairResolver<User, UserRank> resolver) {
        this.property(name, new UserPlaceholder((user) -> resolver.resolve(user, user.getRank())));
        return this;
    }

    protected UserPlaceholders userProperty(Collection<String> names, PairResolver<User, UserRank> resolver) {
        names.forEach(name -> this.userProperty(name, resolver));
        return this;
    }

    protected UserPlaceholders playerProperty(String name, MonoResolver<Player> resolver) {
        this.userProperty(name, (user) -> resolver.resolve(Bukkit.getPlayer(user.getUUID())));
        return this;
    }

    protected UserPlaceholders playerProperty(Collection<String> names, MonoResolver<Player> resolver) {
        names.forEach(name -> this.playerProperty(name, resolver));
        return this;
    }

    protected UserPlaceholders playerOptionProperty(String name, MonoResolver<Option<Player>> resolver) {
        this.userProperty(name, (user) -> resolver.resolve(Option.of(Bukkit.getPlayer(user.getUUID()))));
        return this;
    }

    protected UserPlaceholders playerOptionProperty(Collection<String> names, MonoResolver<Option<Player>> resolver) {
        names.forEach(name -> this.playerOptionProperty(name, resolver));
        return this;
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

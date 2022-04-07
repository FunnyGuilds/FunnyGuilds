package net.dzikoysk.funnyguilds.feature.placeholders;

import java.time.OffsetDateTime;
import java.time.format.TextStyle;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.Entity;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.hooks.HookManager;
import net.dzikoysk.funnyguilds.feature.hooks.vault.VaultHook;
import net.dzikoysk.funnyguilds.feature.placeholders.placeholder.FallbackPlaceholder;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.GuildRankManager;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.rank.DefaultTops;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.MinecraftServerUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import net.dzikoysk.funnyguilds.user.UserRankManager;
import net.dzikoysk.funnyguilds.user.UserUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import panda.std.Pair;
import panda.utilities.StringUtils;
import panda.utilities.text.Joiner;

public class PlaceholdersService {

    private final PluginConfiguration config;
    private final MessageConfiguration messages;

    private final UserManager userManager;
    private final GuildManager guildManager;
    private final UserRankManager userRankManager;
    private final GuildRankManager guildRankManager;

    private SimplePlaceholders<Object> simplePlaceholders;
    private SimplePlaceholders<String> onlinePlaceholders;

    private OffsetDateTimePlaceholders timePlaceholders;

    private UserPlaceholders userPlaceholders;
    private UserPlaceholders playerPlaceholders;

    private GuildPlaceholders simpleGuildPlaceholders;
    private GuildPlaceholders guildPlaceholders;
    private GuildPlaceholders guildAlliesEnemiesPlaceholders;

    private SimplePlaceholders<Pair<String, Guild>> guildMembersColorContextPlaceholders;

    private UserPlaceholders tablistPlaceholders;

    private static final Locale POLISH_LOCALE = new Locale("pl", "PL");

    private static final BiFunction<Collection<String>, String, String> JOIN_OR_DEFAULT = (list, listNoValue) -> list.isEmpty()
            ? listNoValue
            : Joiner.on(", ").join(list).toString();

    public PlaceholdersService(FunnyGuilds plugin) {
        this.config = plugin.getPluginConfiguration();
        this.messages = plugin.getMessageConfiguration();

        this.userManager = plugin.getUserManager();
        this.guildManager = plugin.getGuildManager();
        this.userRankManager = plugin.getUserRankManager();
        this.guildRankManager = plugin.getGuildRankManager();

        this.installPlaceholders();
    }

    public void installPlaceholders() {
        this.installSimplePlaceholders();
        this.installTimePlaceholders();
        this.installOnlinePlaceholders();

        this.installUserPlaceholders();
        this.installPlayerPlaceholders();

        this.installSimpleGuildPlaceholders();
        this.installGuildPlaceholders();
        this.installGuildAlliesEnemiesPlaceholders();

        this.installGuildMembersPlaceholders();

        this.installTablistPlaceholders();
    }

    public SimplePlaceholders<Object> getSimplePlaceholders() {
        return this.simplePlaceholders;
    }

    public SimplePlaceholders<Object> installSimplePlaceholders() {
        return this.simplePlaceholders = new SimplePlaceholders<>()
                .property("tps", MinecraftServerUtils::getFormattedTPS)
                .property("online", () -> Bukkit.getOnlinePlayers().size())
                .property("users", this.userManager::countUsers)
                .property("guilds", this.guildManager::countGuilds);
    }

    public OffsetDateTimePlaceholders getTimePlaceholders() {
        return this.timePlaceholders;
    }

    public OffsetDateTimePlaceholders installTimePlaceholders() {
        return this.timePlaceholders = new OffsetDateTimePlaceholders()
                .timeProperty("hour", OffsetDateTime::getHour)
                .timeProperty("minute", OffsetDateTime::getMinute)
                .timeProperty("second", OffsetDateTime::getSecond)
                .timeProperty("day_of_week", time -> time.getDayOfWeek().getDisplayName(TextStyle.FULL, POLISH_LOCALE))
                .timeProperty("day_of_month", OffsetDateTime::getDayOfMonth)
                .timeProperty("month", time -> time.getMonth().getDisplayName(TextStyle.FULL, POLISH_LOCALE))
                .timeProperty("month_number", OffsetDateTime::getMonthValue)
                .timeProperty("year", OffsetDateTime::getYear);
    }

    public SimplePlaceholders<String> getOnlinePlaceholders() {
        return this.onlinePlaceholders;
    }

    public SimplePlaceholders<String> installOnlinePlaceholders() {
        return this.onlinePlaceholders = new SimplePlaceholders<String>()
                .property("<online>", () -> ChatColor.GREEN)
                .property("</online>", end -> end);
    }

    public UserPlaceholders getUserPlaceholders() {
        return this.userPlaceholders;
    }

    public UserPlaceholders installUserPlaceholders() {
        return this.userPlaceholders = new UserPlaceholders()
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

    public UserPlaceholders getPlayerPlaceholders() {
        return this.playerPlaceholders;
    }

    public UserPlaceholders installPlayerPlaceholders() {
        this.playerPlaceholders = new UserPlaceholders()
                .playerOptionProperty("world", playerOption -> playerOption
                        .map(Player::getWorld)
                        .map(World::getName)
                        .orElseGet(""))
                .playerOptionProperty("online", playerOption -> playerOption
                        .map(player -> Bukkit.getOnlinePlayers().stream().filter(player::canSee).count())
                        .orElseGet(0L));

        if (HookManager.WORLD_GUARD.isPresent()) {
            String wgRegionNoValue = messages.wgRegionNoValue;

            this.playerPlaceholders.playerProperty("wg-region", player -> {
                List<String> regionNames = getWorldGuardRegionNames(player);
                return !regionNames.isEmpty() ? regionNames.get(0) : wgRegionNoValue;
            });

            this.playerPlaceholders.playerProperty("wg-regions", player -> {
                List<String> regionNames = getWorldGuardRegionNames(player);
                return !regionNames.isEmpty() ? Joiner.on(", ").join(regionNames) : wgRegionNoValue;
            });
        }

        if (HookManager.VAULT.isPresent() && VaultHook.isEconomyHooked()) {
            this.playerPlaceholders.playerOptionProperty("vault-money", playerOption -> playerOption
                    .map(VaultHook::accountBalance)
                    .map(value -> Double.toString(value))
                    .orElseGet(""));
        }

        return this.playerPlaceholders;
    }

    public GuildPlaceholders getSimpleGuildPlaceholders() {
        return this.simpleGuildPlaceholders;
    }

    public GuildPlaceholders installSimpleGuildPlaceholders() {
        return this.simpleGuildPlaceholders = new GuildPlaceholders()
                .property("name", Guild::getName, () -> messages.gNameNoValue)
                .property("guild", Guild::getName, () -> messages.gNameNoValue)
                .property("tag", Guild::getTag, () -> messages.gTagNoValue);
    }

    public GuildPlaceholders getGuildPlaceholders() {
        return this.guildPlaceholders;
    }

    public GuildPlaceholders installGuildPlaceholders() {
        Function<Guild, Object> guildProtection = guild -> {
            long now = System.currentTimeMillis();
            long protectionEndTime = guild.getProtection();

            return protectionEndTime < now
                    ? "Brak"
                    : TimeUtils.getDurationBreakdown(protectionEndTime - now);
        };

        return this.guildPlaceholders = this.getSimpleGuildPlaceholders()
                .property("validity",
                        guild -> messages.dateFormat.format(new Date(guild.getValidity())),
                        () -> messages.gValidityNoValue)
                .property("protection", guildProtection::apply, () -> "Brak")
                .property("guild-protection", guildProtection::apply, () -> "Brak")
                .property("owner", guild -> guild.getOwner().getName(), () -> messages.gOwnerNoValue)
                .property("deputies",
                        guild -> JOIN_OR_DEFAULT.apply(Entity.names(guild.getDeputies()), messages.gDeputiesNoValue),
                        () -> messages.gDeputiesNoValue)
                .property("deputy",
                        guild -> guild.getDeputies().isEmpty()
                                ? messages.gDeputiesNoValue
                                : guild.getDeputies().iterator().next().getName(),
                        () -> messages.gDeputiesNoValue)
                .property("members-online", guild -> guild.getOnlineMembers().size(), () -> 0)
                .property("members-all", guild -> guild.getMembers().size(), () -> 0)
                .property("allies-all", guild -> guild.getAllies().size(), () -> 0)
                .property("enemies-all", guild -> guild.getEnemies().size(), () -> 0)
                .property("region-size",
                        guild -> guild.getRegion()
                                .map(Region::getSize)
                                .map(value -> Integer.toString(value))
                                .orElseGet(messages.gRegionSizeNoValue),
                        () -> messages.gRegionSizeNoValue)
                .property("lives", Guild::getLives, () -> 0)
                .property("lives-symbol",
                        guild -> {
                            int lives = guild.getLives();
                            if (lives <= config.warLives) {
                                return StringUtils.repeated(lives, config.livesRepeatingSymbol.full.getValue()) +
                                        StringUtils.repeated(config.warLives - lives, config.livesRepeatingSymbol.empty.getValue());
                            }
                            else {
                                return StringUtils.repeated(config.warLives, config.livesRepeatingSymbol.full.getValue()) +
                                        config.livesRepeatingSymbol.more.getValue();
                            }
                        }, () -> messages.livesNoValue)
                .property("lives-symbol-all",
                        guild -> StringUtils.repeated(guild.getLives(), config.livesRepeatingSymbol.full.getValue()),
                        () -> messages.livesNoValue)
                .property("position",
                        (guild, rank) -> guildRankManager.isRankedGuild(guild)
                                ? String.valueOf(rank.getPosition(DefaultTops.GUILD_AVG_POINTS_TOP))
                                : messages.minMembersToIncludeNoValue,
                        () -> messages.minMembersToIncludeNoValue)
                .property("rank",
                        (guild, rank) -> guildRankManager.isRankedGuild(guild)
                                ? String.valueOf(rank.getPosition(DefaultTops.GUILD_AVG_POINTS_TOP))
                                : messages.minMembersToIncludeNoValue,
                        () -> messages.minMembersToIncludeNoValue)
                //TODO total-points -> points (I know this will break up backwards compatibility so I'm not doing it now)
                .property("total-points", (guild, rank) -> rank.getPoints(), () -> 0)
                .property("avg-points", (guild, rank) -> rank.getAveragePoints(), () -> 0)
                .property("points", (guild, rank) -> rank.getAveragePoints(), () -> 0)
                .property("points-format",
                        (guild, rank) -> NumberRange.inRangeToString(rank.getAveragePoints(), config.pointsFormat)
                                .replace("{POINTS}", String.valueOf(guild.getRank().getAveragePoints())),
                        () -> NumberRange.inRangeToString(0, config.pointsFormat)
                                .replace("{POINTS}", "0"))
                .property("kills", (guild, rank) -> rank.getKills(), () -> 0)
                .property("avg-kills", (guild, rank) -> rank.getAverageKills(), () -> 0)
                .property("deaths", (guild, rank) -> rank.getDeaths(), () -> 0)
                .property("avg-deaths", (guild, rank) -> rank.getAverageDeaths(), () -> 0)
                .property("assists", (guild, rank) -> rank.getAssists(), () -> 0)
                .property("avg-assists", (guild, rank) -> rank.getAverageAssists(), () -> 0)
                .property("logouts", (guild, rank) -> rank.getLogouts(), () -> 0)
                .property("avg-logouts", (guild, rank) -> rank.getAverageLogouts(), () -> 0)
                .property("kdr", (guild, rank) -> String.format(Locale.US, "%.2f", rank.getKDR()), () -> 0.0)
                .property("avg-kdr", (guild, rank) -> String.format(Locale.US, "%.2f", rank.getAverageKDR()), () -> 0.0);
    }

    public GuildPlaceholders getGuildAlliesEnemiesPlaceholders() {
        return this.guildAlliesEnemiesPlaceholders;
    }

    public GuildPlaceholders installGuildAlliesEnemiesPlaceholders() {
        return this.guildAlliesEnemiesPlaceholders = new GuildPlaceholders()
                .property("allies",
                        guild -> JOIN_OR_DEFAULT.apply(Entity.names(guild.getAllies()), messages.alliesNoValue),
                        () -> messages.alliesNoValue)
                .property("allies-tags",
                        guild -> JOIN_OR_DEFAULT.apply(GuildUtils.getTags(guild.getAllies()), messages.alliesNoValue),
                        () -> messages.alliesNoValue)
                .property("enemies",
                        guild -> JOIN_OR_DEFAULT.apply(Entity.names(guild.getEnemies()), messages.enemiesNoValue),
                        () -> messages.enemiesNoValue)
                .property("enemies-tags",
                        guild -> JOIN_OR_DEFAULT.apply(GuildUtils.getTags(guild.getEnemies()), messages.enemiesNoValue),
                        () -> messages.enemiesNoValue);
    }

    public SimplePlaceholders<Pair<String, Guild>> getGuildMembersPlaceholders() {
        return this.guildMembersColorContextPlaceholders;
    }

    public SimplePlaceholders<Pair<String, Guild>> installGuildMembersPlaceholders() {
        return this.guildMembersColorContextPlaceholders = new SimplePlaceholders<Pair<String, Guild>>()
                .property("members", pair -> {
                    String text = JOIN_OR_DEFAULT.apply(UserUtils.getOnlineNames(pair.getSecond().getMembers()), "");

                    return !text.contains("<online>")
                            ? text
                            : this.onlinePlaceholders.toFormatter(pair.getFirst()).format(text);
                });
    }

    public UserPlaceholders getTablistPlaceholders() {
        return this.tablistPlaceholders;
    }

    public UserPlaceholders installTablistPlaceholders() {
        return this.tablistPlaceholders = new UserPlaceholders()
                .map(this.simplePlaceholders, () -> null)
                .map(this.timePlaceholders, () -> OffsetDateTime.now().plusHours(config.timeOffset))
                .property(this.userPlaceholders.getPlaceholders())
                .property(this.playerPlaceholders.getPlaceholders())
                .map(this.guildPlaceholders, name -> "G-" + name, (user, placeholder) ->
                        user.getGuild()
                                .map(placeholder::getRaw)
                                .orElseGet(() -> placeholder instanceof FallbackPlaceholder
                                        ? ((FallbackPlaceholder<?>) placeholder).getRawFallback()
                                        : ""));
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

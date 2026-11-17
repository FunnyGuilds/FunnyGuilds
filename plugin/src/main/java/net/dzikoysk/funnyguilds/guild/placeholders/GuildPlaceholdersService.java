package net.dzikoysk.funnyguilds.guild.placeholders;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.base.Joiner;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.Entity;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.message.EntityLocaleProvider;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.feature.placeholders.StaticPlaceholdersService;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.rank.GuildStatAggregationType;
import net.dzikoysk.funnyguilds.rank.RankService;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.permission.GenericGuildPermissions;
import net.dzikoysk.funnyguilds.guild.permission.GuildPermissionChecker;
import net.dzikoysk.funnyguilds.shared.adventure.ComponentUtil;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import net.dzikoysk.funnyguilds.user.UserStatKeys;
import net.dzikoysk.funnyguilds.user.UserUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class GuildPlaceholdersService extends StaticPlaceholdersService<Guild, GuildPlaceholders> {

    private static final int DEFAULT_MEMBER_PRIORITY = Integer.MAX_VALUE;

    private static Optional<GuildPlaceholders> SIMPLE = Optional.empty();

    public GuildPlaceholdersService(EntityLocaleProvider entityLocaleProvider) {
        super(entityLocaleProvider);
    }

    public static Optional<GuildPlaceholders> getSimplePlaceholders() {
        return SIMPLE;
    }

    public static GuildPlaceholders createSimplePlaceholders(FunnyGuilds plugin) {
        MessageService messages = plugin.getMessageService();

        GuildPlaceholders placeholders = new GuildPlaceholders()
                .property("name", Guild::getName, entity -> messages.getComponent(entity, config -> config.gNameNoValue))
                .property("guild", Guild::getName, entity -> messages.getComponent(entity, config -> config.gNameNoValue))
                .property("tag", Guild::getTag, entity -> messages.getComponent(entity, config -> config.gTagNoValue));
        SIMPLE = Optional.of(placeholders);

        return placeholders;
    }

    public static GuildPlaceholders createGuildPlaceholders(FunnyGuilds plugin) {
        PluginConfiguration pluginConfiguration = plugin.getPluginConfiguration();
        MessageService messages = plugin.getMessageService();
        RankService rankManager = plugin.getRankService();

        return new GuildPlaceholders()
                .property("owner", guild -> guild.getOwner().getName(), entity -> messages.get(entity, config -> config.gOwnerNoValue))
                .property("deputies",
                        (entity, guild) -> ComponentUtil.joinColoredOrDefault(
                                Entity.names(guild.getDeputies()),
                                null
                        ),
                        entity -> messages.get(entity, config -> config.gDeputiesNoValue))
                .property("deputy",
                        (entity, guild) -> guild.getDeputies()
                                .stream()
                                .findFirst()
                                .map(User::getName)
                                .orElse(null),
                        entity -> messages.get(entity, config -> config.gDeputyNoValue))
                .property("members", (entity, guild) -> ComponentUtil.join(UserUtils.getOnlineNames(guild.getMembers()), ", "), 
                        entity -> messages.get(entity, config -> config.gMembersNoValue))
                .property("members-online", guild -> guild.getOnlineMembers(true).size(), entity -> 0)
                .property("members-all", guild -> guild.getMembers().size(), entity -> 0)
                .property("allies", 
                        (entity, guild) -> {
                            Collection<Guild> allies = guild.getAllies();
                            if (allies.isEmpty()) {
                                return null;
                            }
                            return Joiner.on(", ").join(Entity.names(allies));
                        },
                        entity -> messages.get(entity, config -> config.alliesNoValue))
                .property("allies-tags",
                        (entity, guild) -> {
                            Collection<Guild> allies = guild.getAllies();
                            if (allies.isEmpty()) {
                                return null;
                            }
                            return Joiner.on(", ").join(GuildUtils.getTags(allies));
                        },
                        entity -> messages.get(entity, config -> config.alliesNoValue))
                .property("allies-all", guild -> guild.getAllies().size(), entity -> 0)
                .property("enemies",
                        (entity, guild) -> {
                            Collection<Guild> enemies = guild.getEnemies();
                            if (enemies.isEmpty()) {
                                return null;
                            }
                            return Joiner.on(", ").join(Entity.names(enemies));
                        },
                        entity -> messages.get(entity, config -> config.enemiesNoValue))
                .property("enemies-tags",
                        (entity, guild) -> {    
                            Collection<Guild> enemies = guild.getEnemies();
                            if (enemies.isEmpty()) {
                                return null;
                            }
                            return Joiner.on(", ").join(GuildUtils.getTags(enemies));
                        },
                        entity -> messages.get(entity, config -> config.enemiesNoValue))
                .property("enemies-all", guild -> guild.getEnemies().size(), entity -> 0)
                .property("region-size",
                        (entity, guild) -> guild.getRegion()
                                .map(Region::getSize)
                                .map(value -> Integer.toString(value))
                                .orElse(null),
                       entity -> messages.get(entity, config -> config.gRegionSizeNoValue))
                .property("pvp",
                        (entity, guild) -> guild.hasPvPEnabled()
                                ? messages.get(entity, config -> config.pvpStatusOn)
                                : messages.get(entity, config -> config.pvpStatusOff),
                        entity -> messages.get(entity, config -> config.pvpStatusOff))
                .timeProperty(
                        "validity",
                        Guild::getValidity,
                        messages,
                        entity -> messages.get(entity, config -> config.gValidityNoValue)
                )
                .timeProperty(
                        "protection",
                        Guild::getProtection,
                        messages,
                        entity -> messages.get(entity, config -> config.gProtectionNoValue)
                )
                .property("lives", Guild::getLives, entity -> 0)
                .property("war-max-lives", guild -> pluginConfiguration.war.maxLives, entity -> 0)
                .numericProperty("wars-won", "wars_won", Guild::getWarsWon, pluginConfiguration::getValueFormatting)
                .numericProperty("wars-lost", "wars_lost", Guild::getWarsLost, pluginConfiguration::getValueFormatting)
                .property("wars-last",
                        (entity, guild) -> guild.getLastWarGuildTag().orElse(null),
                        entity -> messages.get(entity, config -> config.enemiesNoValue))
                .numericProperty("war-streak", "war_streak", Guild::getWarStreak, pluginConfiguration::getValueFormatting)
                .property("heart-lives", Guild::getHeartLives, entity -> 0)
                .property("heart-lives-max", guild -> pluginConfiguration.war.heartLives, entity -> 0)
                .property("lives-symbol",
                        guild -> {
                            int lives = guild.getLives();
                            if (lives <= pluginConfiguration.war.lives) {
                                return Component.text()
                                        .append(ComponentUtil.repeat(
                                                pluginConfiguration.livesRepeatingSymbol.full,
                                                lives
                                        ))
                                        .append(ComponentUtil.repeat(
                                                pluginConfiguration.livesRepeatingSymbol.empty, 
                                                pluginConfiguration.war.lives - lives
                                        ));
                            } else {
                                return Component.text()
                                        .append(ComponentUtil.repeat(
                                                pluginConfiguration.livesRepeatingSymbol.full,
                                                pluginConfiguration.war.lives
                                        ))
                                        .append(pluginConfiguration.livesRepeatingSymbol.more);
                            }
                        }, entity -> messages.get(config -> config.livesNoValue))
                .property("lives-symbol-all",
                        guild -> ComponentUtil.repeat(pluginConfiguration.livesRepeatingSymbol.full, guild.getLives()),
                        entity -> messages.get(entity, config -> config.livesNoValue))
                .property("position",
                        (entity, guild) -> rankManager.isRankedGuild(guild)
                                ? String.valueOf(rankManager.getGuildTopPosition(guild, UserStatKeys.POINTS, GuildStatAggregationType.AVERAGE_INT).orElse(0))
                                : messages.get(entity, config -> config.minMembersToIncludeNoValue),
                        entity -> messages.get(entity, config -> config.minMembersToIncludeNoValue))
                .property("rank",
                        (entity, guild) -> rankManager.isRankedGuild(guild)
                                ? String.valueOf(rankManager.getGuildTopPosition(guild, UserStatKeys.POINTS, GuildStatAggregationType.AVERAGE_INT).orElse(0))
                                : messages.get(entity, config -> config.minMembersToIncludeNoValue),
                        entity -> messages.get(entity, config -> config.minMembersToIncludeNoValue))
                .numericRankProperty("points", "points", guild -> rankManager.getGuildStatValue(guild, UserStatKeys.POINTS, GuildStatAggregationType.TOTAL_LONG).orElse(0L), 0, pluginConfiguration::getValueFormatting)
                .numericRankProperty("avg-points", "avg_points", guild -> rankManager.getGuildStatValue(guild, UserStatKeys.POINTS, GuildStatAggregationType.AVERAGE_INT).orElse(0), 0, pluginConfiguration::getValueFormatting)
                .numericRankProperty("kills", "kills", guild -> rankManager.getGuildStatValue(guild, UserStatKeys.KILLS, GuildStatAggregationType.TOTAL_LONG).orElse(0L), 0, pluginConfiguration::getValueFormatting)
                .numericRankProperty("avg-kills", "avg_kills", guild -> rankManager.getGuildStatValue(guild, UserStatKeys.KILLS, GuildStatAggregationType.AVERAGE_INT).orElse(0), 0, pluginConfiguration::getValueFormatting)
                .numericRankProperty("deaths", "deaths", guild -> rankManager.getGuildStatValue(guild, UserStatKeys.DEATHS, GuildStatAggregationType.TOTAL_LONG).orElse(0L), 0, pluginConfiguration::getValueFormatting)
                .numericRankProperty("avg-deaths", "avg_deaths", guild -> rankManager.getGuildStatValue(guild, UserStatKeys.DEATHS, GuildStatAggregationType.AVERAGE_INT).orElse(0), 0, pluginConfiguration::getValueFormatting)
                .numericRankProperty("assists", "assists", guild -> rankManager.getGuildStatValue(guild, UserStatKeys.ASSISTS, GuildStatAggregationType.TOTAL_LONG).orElse(0L), 0, pluginConfiguration::getValueFormatting)
                .numericRankProperty("avg-assists", "avg_assists", guild -> rankManager.getGuildStatValue(guild, UserStatKeys.ASSISTS, GuildStatAggregationType.AVERAGE_INT).orElse(0), 0, pluginConfiguration::getValueFormatting)
                .numericRankProperty("logouts", "logouts", guild -> rankManager.getGuildStatValue(guild, UserStatKeys.LOGOUTS, GuildStatAggregationType.TOTAL_LONG).orElse(0L), 0, pluginConfiguration::getValueFormatting)
                .numericRankProperty("avg-logouts", "avg_logouts", guild -> rankManager.getGuildStatValue(guild, UserStatKeys.LOGOUTS, GuildStatAggregationType.AVERAGE_INT).orElse(0), 0, pluginConfiguration::getValueFormatting)
                .numericRankProperty("kdr", "kdr", guild -> rankManager.getGuildStatValue(guild, UserStatKeys.KDR, GuildStatAggregationType.TOTAL_DOUBLE).orElse(0.0), 0.00, pluginConfiguration::getValueFormatting)
                .numericRankProperty("avg-kdr", "avg_kdr", guild -> rankManager.getGuildStatValue(guild, UserStatKeys.KDR, GuildStatAggregationType.AVERAGE).orElse(0.0), 0.00, pluginConfiguration::getValueFormatting)
                .numericRankProperty("kda", "kda", guild -> rankManager.getGuildStatValue(guild, UserStatKeys.KDA, GuildStatAggregationType.TOTAL_DOUBLE).orElse(0.0), 0.00, pluginConfiguration::getValueFormatting)
                .numericRankProperty("avg-kda", "avg_kda", guild -> rankManager.getGuildStatValue(guild, UserStatKeys.KDA, GuildStatAggregationType.AVERAGE).orElse(0.0), 0.00, pluginConfiguration::getValueFormatting);
    }

    public static GuildPlaceholders createMemberPlaceholders(FunnyGuilds plugin) {
        PluginConfiguration config = plugin.getPluginConfiguration();
        MessageService messages = plugin.getMessageService();
        GuildManager guildManager = plugin.getGuildManager();
        UserManager userManager = plugin.getUserManager();
        GuildPermissionChecker permissionChecker = plugin.getGuildPermissionChecker();

        LoadingCache<MemberPriorityKey, Integer> priorityCache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build(key -> guildManager.findByUuid(key.guildUuid())
                        .flatMap(guild -> userManager.findByUuid(key.userUuid())
                                .flatMap(user -> permissionChecker.getPermissionValue(guild, user, GenericGuildPermissions.MEMBER_LIST_PRIORITY)))
                        .orElse(DEFAULT_MEMBER_PRIORITY));

        GuildPlaceholders placeholders = new GuildPlaceholders();
        for (int i = 1; i <= config.maxMembersInGuild; i++) {
            int index = i;
            placeholders = placeholders.property("member-" + index,
                    (entity, guild) -> {
                        List<MemberView> sorted = sortedMembers(guild, config, priorityCache);
                        if (index > sorted.size()) {
                            return messages.get(entity, msgConfig -> msgConfig.gMemberNoValue);
                        }
                        MemberView view = sorted.get(index - 1);
                        TextColor color = view.online() ? config.onlineColor : config.offlineColor;
                        return Component.text(view.user().getName(), color);
                    },
                    entity -> messages.get(entity, msgConfig -> msgConfig.gMemberNoValue));
        }
        return placeholders;
    }

    private static boolean isOnline(User user, PluginConfiguration config) {
        if (!user.isOnline()) {
            return false;
        }
        return !(config.gMemberRespectVanish && user.isVanished());
    }

    private static List<MemberView> sortedMembers(Guild guild, PluginConfiguration config, LoadingCache<MemberPriorityKey, Integer> priorityCache) {
        return guild.getMembers().stream()
                .map(user -> new MemberView(
                        user,
                        isOnline(user, config),
                        priorityCache.get(new MemberPriorityKey(guild.getUUID(), user.getUUID()))))
                .sorted(Comparator
                        .comparingInt((MemberView view) -> view.online() ? 0 : 1)
                        .thenComparingInt(MemberView::priority)
                        .thenComparing(view -> view.user().getName(), String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    private record MemberView(User user, boolean online, int priority) {
    }

    private record MemberPriorityKey(UUID guildUuid, UUID userUuid) {
    }
}

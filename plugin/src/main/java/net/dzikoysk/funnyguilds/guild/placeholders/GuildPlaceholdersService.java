package net.dzikoysk.funnyguilds.guild.placeholders;

import com.google.common.base.Joiner;
import java.util.Collection;
import net.dzikoysk.funnyguilds.Entity;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.message.EntityLocaleProvider;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.feature.placeholders.StaticPlaceholdersService;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildRank;
import net.dzikoysk.funnyguilds.guild.GuildRankManager;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.rank.DefaultTops;
import net.dzikoysk.funnyguilds.shared.adventure.ComponentUtil;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserUtils;
import net.kyori.adventure.text.Component;
import panda.std.Option;

public class GuildPlaceholdersService extends StaticPlaceholdersService<Guild, GuildPlaceholders> {

    private static Option<GuildPlaceholders> SIMPLE = Option.none();

    public GuildPlaceholdersService(EntityLocaleProvider entityLocaleProvider) {
        super(entityLocaleProvider);
    }

    public static Option<GuildPlaceholders> getSimplePlaceholders() {
        return SIMPLE;
    }

    public static GuildPlaceholders createSimplePlaceholders(FunnyGuilds plugin) {
        MessageService messages = plugin.getMessageService();

        GuildPlaceholders placeholders = new GuildPlaceholders()
                .property("name", Guild::getName, entity -> messages.getComponent(entity, config -> config.gNameNoValue))
                .property("guild", Guild::getName, entity -> messages.getComponent(entity, config -> config.gNameNoValue))
                .property("tag", Guild::getTag, entity -> messages.getComponent(entity, config -> config.gTagNoValue));
        SIMPLE = Option.of(placeholders);

        return placeholders;
    }

    public static GuildPlaceholders createGuildPlaceholders(FunnyGuilds plugin) {
        PluginConfiguration pluginConfiguration = plugin.getPluginConfiguration();
        MessageService messages = plugin.getMessageService();
        GuildRankManager rankManager = plugin.getGuildRankManager();

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
                .property("members", (entity, guild) -> ComponentUtil.join(UserUtils.getOnlineNames(guild.getMembers()), ", "))
                .property("members-online", guild -> guild.getOnlineMembers().size(), entity -> 0)
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
                                .orNull(),
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
                .property("lives-symbol",
                        guild -> {
                            int lives = guild.getLives();
                            if (lives <= pluginConfiguration.warLives) {
                                return Component.text()
                                        .append(ComponentUtil.repeat(
                                                pluginConfiguration.livesRepeatingSymbol.full,
                                                lives
                                        ))
                                        .append(ComponentUtil.repeat(
                                                pluginConfiguration.livesRepeatingSymbol.empty, 
                                                pluginConfiguration.warLives - lives
                                        ));
                            } else {
                                return Component.text()
                                        .append(ComponentUtil.repeat(
                                                pluginConfiguration.livesRepeatingSymbol.full,
                                                pluginConfiguration.warLives
                                        ))
                                        .append(pluginConfiguration.livesRepeatingSymbol.more);
                            }
                        }, entity -> messages.get(config -> config.livesNoValue))
                .property("lives-symbol-all",
                        guild -> ComponentUtil.repeat(pluginConfiguration.livesRepeatingSymbol.full, guild.getLives()),
                        entity -> messages.get(entity, config -> config.livesNoValue))
                .rankProperty("position",
                        (entity, guild, rank) -> rankManager.isRankedGuild(guild)
                                ? String.valueOf(rank.getPosition(DefaultTops.GUILD_AVG_POINTS_TOP))
                                : messages.get(entity, config -> config.minMembersToIncludeNoValue),
                        entity -> messages.get(entity, config -> config.minMembersToIncludeNoValue))
                .rankProperty("rank",
                        (entity, guild, rank) -> rankManager.isRankedGuild(guild)
                                ? String.valueOf(rank.getPosition(DefaultTops.GUILD_AVG_POINTS_TOP))
                                : messages.get(entity, config -> config.minMembersToIncludeNoValue),
                        entity -> messages.get(entity, config -> config.minMembersToIncludeNoValue))
                .rankProperty("points", GuildRank::getPoints, 0)
                .rankProperty("avg-points", GuildRank::getAveragePoints,0)
                .rankProperty("kills", GuildRank::getKills, 0)
                .rankProperty("avg-kills", GuildRank::getAverageKills, 0)
                .rankProperty("deaths", GuildRank::getDeaths, 0)
                .rankProperty("avg-deaths", GuildRank::getAverageDeaths, 0)
                .rankProperty("assists", GuildRank::getAssists, 0)
                .rankProperty("avg-assists", GuildRank::getAverageAssists, 0)
                .rankProperty("logouts", GuildRank::getLogouts, 0)
                .rankProperty("avg-logouts", GuildRank::getAverageLogouts, 0)
                .rankProperty("kdr", GuildRank::getKDR, 0.00)
                .rankProperty("avg-kdr", GuildRank::getAverageKDR, 0.00)
                .rankProperty("kda", GuildRank::getKDA, 0.00)
                .rankProperty("avg-kda", GuildRank::getAverageKDA, 0.00);
    }
}

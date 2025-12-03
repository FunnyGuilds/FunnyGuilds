package net.dzikoysk.funnyguilds.guild.placeholders;

import java.util.LinkedHashSet;
import java.util.Set;
import net.dzikoysk.funnyguilds.Entity;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.feature.placeholders.StaticPlaceholdersService;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildRank;
import net.dzikoysk.funnyguilds.guild.GuildRankManager;
import net.dzikoysk.funnyguilds.rank.DefaultTops;
import net.dzikoysk.funnyguilds.shared.adventure.ComponentUtil;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;

public class GuildPlaceholdersService extends StaticPlaceholdersService<Guild, GuildPlaceholders> {

    private static Option<GuildPlaceholders> SIMPLE = Option.none();

    @Override
    public Component format(@Nullable Object entity, Component text, Guild guild) {
        text = super.format(entity, text, guild);
//        text = GUILD_MEMBERS_COLOR_CONTEXT.toVariablesFormatter(Pair.of(ChatUtils.getLastColorBefore(text, "{MEMBERS}"), guild))
//                .format(text);
        //TODO: reimplement
        return text;
    }

    public static Option<GuildPlaceholders> getSimplePlaceholders() {
        return SIMPLE;
    }

    public static GuildPlaceholders createSimplePlaceholders(FunnyGuilds plugin) {
        MessageService messages = plugin.getMessageService();

        GuildPlaceholders placeholders = new GuildPlaceholders()
                .property("name", Guild::getName, entity -> messages.get(entity, config -> config.gNameNoValue))
                .property("guild", Guild::getName, entity -> messages.get(entity, config -> config.gNameNoValue))
                .property("tag", Guild::getTag, entity -> messages.get(entity, config -> config.gTagNoValue));
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
                        (entity, guild) -> ComponentUtil.joinColoredOrDefault(Entity.names(guild.getDeputies()), messages.get(entity, config -> config.gDeputiesNoValue)),
                        entity -> messages.get(entity, config -> config.gDeputiesNoValue))
                .property("deputy",
                        (entity, guild) -> guild.getDeputies().isEmpty()
                                ? messages.get(entity, config -> config.gDeputyNoValue)
                                : guild.getDeputies().iterator().next().getName(),
                        entity -> messages.get(entity, config -> config.gDeputyNoValue))
                .property("members-online", guild -> guild.getOnlineMembers().size(), entity -> 0)
                .property("members-all", guild -> guild.getMembers().size(), entity -> 0)
                .property("allies-all", guild -> guild.getAllies().size(), entity -> 0)
                .property("enemies-all", guild -> guild.getEnemies().size(), entity -> 0)
//                .property("region-size",
//                        (entity, guild) -> guild.getRegion()
//                                .map(Region::getSize)
//                                .map(value -> Integer.toString(value))
//                                .orElseGet(messages.<String>get(entity, config -> config.gRegionSizeNoValue)),
//                        entity -> messages.get(entity, config -> config.gRegionSizeNoValue))
                .property("pvp",
                        (entity, guild) -> guild.hasPvPEnabled()
                                ? messages.get(entity, config -> config.pvpStatusOn)
                                : messages.get(entity, config -> config.pvpStatusOff),
                        entity -> messages.get(entity, config -> config.pvpStatusOff))
//                .timeProperty("validity", Guild::getValidity, messages, config -> config.gValidityNoValue)
//                .timeProperty("protection", Guild::getProtection, messages, config -> config.gProtectionNoValue)
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

    public static GuildPlaceholders createAlliesEnemiesPlaceholders(FunnyGuilds plugin) {
        MessageService messages = plugin.getMessageService();
        return new GuildPlaceholders();
//                .property("allies",
//                        (entity, guild) -> ComponentUtil.joinColoredOrDefault(Entity.names(guild.getAllies()), messages.get(entity, config -> config.alliesNoValue)),
//                        entity -> messages.get(entity, config -> config.alliesNoValue))
//                .property("allies-tags",
//                        (entity, guild) -> ComponentUtil.join(GuildUtils.getTags(guild.getAllies()), messages.get(entity, config -> config.alliesNoValue)),
//                        entity -> messages.get(entity, config -> config.alliesNoValue))
//                .property("enemies",
//                        (entity, guild) -> JOIN_OR_DEFAULT.apply(Entity.names(guild.getEnemies()), messages.get(entity, config -> config.enemiesNoValue)),
//                        entity -> messages.get(entity, config -> config.enemiesNoValue))
//                .property("enemies-tags",
//                        (entity, guild) -> JOIN_OR_DEFAULT.apply(GuildUtils.getTags(guild.getEnemies()), messages.get(entity, config -> config.enemiesNoValue)),
//                        entity -> messages.get(entity, config -> config.enemiesNoValue));
    }

    @Override
    public Set<FunnyFormatter> prepareReplacements(@Nullable Object entity, Guild data) {
        Set<FunnyFormatter> formatters = new LinkedHashSet<>(super.prepareReplacements(entity, data));
        //formatters.add(GUILD_MEMBERS_COLOR_CONTEXT.toVariablesFormatter(Pair.of(ChatColor.RESET.toString(), data)));
        return formatters;
    }

}

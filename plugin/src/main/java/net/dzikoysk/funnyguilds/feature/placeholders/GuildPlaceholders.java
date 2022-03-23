package net.dzikoysk.funnyguilds.feature.placeholders;

import java.util.Arrays;
import java.util.Date;
import net.dzikoysk.funnyguilds.Entity;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.placeholders.impl.Placeholder;
import net.dzikoysk.funnyguilds.feature.placeholders.impl.guild.GuildPlaceholder;
import net.dzikoysk.funnyguilds.feature.placeholders.impl.guild.GuildRankPlaceholder;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildRankManager;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.rank.DefaultTops;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import net.dzikoysk.funnyguilds.user.UserUtils;
import panda.std.Pair;
import panda.utilities.StringUtils;
import panda.utilities.text.Joiner;

public class GuildPlaceholders extends Placeholders<Guild, GuildPlaceholder> {

    public static final Placeholders<Guild, GuildPlaceholder> GUILD;
    public static final Placeholders<Guild, GuildPlaceholder> GUILD_ALL;
    public static final Placeholders<Guild, GuildPlaceholder> GUILD_ALLIES_ENEMIES_ALL;

    public static final Placeholders<Pair<String, Guild>, Placeholder<Pair<String, Guild>>> GUILD_MEMBERS_COLOR_CONTEXT;

    static {
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        PluginConfiguration config = plugin.getPluginConfiguration();
        MessageConfiguration messages = plugin.getMessageConfiguration();
        GuildRankManager guildRankManager = plugin.getGuildRankManager();

        GUILD = new GuildPlaceholders()
                .property("name", new GuildPlaceholder(Guild::getName, () -> messages.gNameNoValue))
                .property("tag", new GuildPlaceholder(Guild::getTag, () -> messages.gTagNoValue));

        GUILD_ALL = new GuildPlaceholders()
                .property("name", new GuildPlaceholder(Guild::getName, () -> messages.gNameNoValue))
                .property("tag", new GuildPlaceholder(Guild::getTag, () -> messages.gTagNoValue))
                .property("validity", new GuildPlaceholder(
                        guild -> messages.dateFormat.format(new Date(guild.getValidity())),
                        () -> messages.gValidityNoValue))
                .property(Arrays.asList("protection", "guild-protection"), new GuildPlaceholder(
                        guild -> {
                            long now = System.currentTimeMillis();
                            long protectionEndTime = guild.getProtection();

                            return protectionEndTime < now ? "Brak" : TimeUtils.getDurationBreakdown(protectionEndTime - now);
                        }, () -> "Brak"))
                .property("owner", new GuildPlaceholder(Guild::getOwner, () -> messages.gOwnerNoValue))
                .property("deputies", new GuildPlaceholder(guild ->
                        guild.getDeputies().isEmpty()
                                ? messages.gDeputiesNoValue
                                : Joiner.on(", ").join(Entity.names(guild.getDeputies())),
                        () -> messages.gDeputiesNoValue))
                .property("deputy", new GuildPlaceholder(
                        guild -> guild.getDeputies().isEmpty()
                                ? messages.gDeputiesNoValue
                                : guild.getDeputies().iterator().next().getName(),
                        () -> messages.gDeputiesNoValue))
                .property("members-online", new GuildPlaceholder(guild -> guild.getOnlineMembers().size(), () -> 0))
                .property("members-all", new GuildPlaceholder(guild -> guild.getMembers().size(), () -> 0))
                .property("allies-all", new GuildPlaceholder(guild -> guild.getAllies().size(), () -> 0))
                .property("enemies-all", new GuildPlaceholder(guild -> guild.getEnemies().size(), () -> 0))
                .property("region-size", new GuildPlaceholder(
                        guild -> guild.getRegion()
                                .map(Region::getSize)
                                .map(value -> Integer.toString(value))
                                .orElseGet(messages.gRegionSizeNoValue),
                        () -> messages.gRegionSizeNoValue))
                .property("lives", new GuildPlaceholder((Guild::getLives), () -> 0))
                .property("lives-symbol", new GuildPlaceholder(guild -> {
                    int lives = guild.getLives();
                    if (lives <= config.warLives) {
                        return StringUtils.repeated(lives, config.livesRepeatingSymbol.full.getValue()) +
                                StringUtils.repeated(config.warLives - lives, config.livesRepeatingSymbol.empty.getValue());
                    }
                    else {
                        return StringUtils.repeated(config.warLives, config.livesRepeatingSymbol.full.getValue()) +
                                config.livesRepeatingSymbol.more.getValue();
                    }
                }, () -> messages.livesNoValue))
                .property("lives-symbol-all", new GuildPlaceholder(
                        guild -> StringUtils.repeated(guild.getLives(), config.livesRepeatingSymbol.full.getValue()),
                        () -> messages.livesNoValue))
                .property(Arrays.asList("position", "rank"), new GuildRankPlaceholder(
                        (guild, rank) -> guildRankManager.isRankedGuild(guild)
                                ? String.valueOf(guild.getRank().getPosition(DefaultTops.GUILD_AVG_POINTS_TOP))
                                : messages.minMembersToIncludeNoValue,
                        () -> messages.minMembersToIncludeNoValue))
                .property("total-points", new GuildRankPlaceholder((guild, rank) -> rank.getPoints(), () -> 0))
                .property(Arrays.asList("avg-points", "points"), new GuildRankPlaceholder((guild, rank) -> rank.getAveragePoints(), () -> 0))
                .property("points-format", new GuildRankPlaceholder(
                        (guild, rank) -> NumberRange.inRangeToString(rank.getAveragePoints(), config.pointsFormat)
                                .replace("{POINTS}", String.valueOf(guild.getRank().getAveragePoints())),
                        () -> NumberRange.inRangeToString(0, config.pointsFormat)
                                .replace("{POINTS}", "0")))
                .property("kills", new GuildRankPlaceholder((guild, rank) -> rank.getKills(), () -> 0))
                .property("avg-kills", new GuildRankPlaceholder((guild, rank) -> rank.getAverageKills(), () -> 0))
                .property("deaths", new GuildRankPlaceholder((guild, rank) -> rank.getDeaths(), () -> 0))
                .property("avg-deaths", new GuildRankPlaceholder((guild, rank) -> rank.getAverageDeaths(), () -> 0))
                .property("assists", new GuildRankPlaceholder((guild, rank) -> rank.getAssists(), () -> 0))
                .property("avg-assists", new GuildRankPlaceholder((guild, rank) -> rank.getAverageAssists(), () -> 0))
                .property("logouts", new GuildRankPlaceholder((guild, rank) -> rank.getLogouts(), () -> 0))
                .property("avg-logouts", new GuildRankPlaceholder((guild, rank) -> rank.getAverageLogouts(), () -> 0))
                .property("kdr", new GuildRankPlaceholder((guild, rank) -> rank.getKDR(), () -> 0))
                .property("avg-kdr", new GuildRankPlaceholder((guild, rank) -> rank.getAverageKDR(), () -> 0));

        GUILD_ALLIES_ENEMIES_ALL = new GuildPlaceholders()
                .property("allies", new GuildPlaceholder(
                        guild -> guild.getAllies().isEmpty()
                                ? messages.alliesNoValue
                                : Joiner.on(", ").join(Entity.names(guild.getAllies())),
                        () -> messages.alliesNoValue))
                .property("allies-tags", new GuildPlaceholder(
                        guild -> guild.getAllies().isEmpty()
                                ? messages.alliesNoValue
                                : Joiner.on(", ").join(GuildUtils.getTags(guild.getAllies())),
                        () -> messages.alliesNoValue))
                .property("enemies", new GuildPlaceholder(
                        guild -> guild.getEnemies().isEmpty()
                                ? messages.enemiesNoValue
                                : Joiner.on(", ").join(Entity.names(guild.getEnemies())),
                        () -> messages.enemiesNoValue))
                .property("enemies-tags", new GuildPlaceholder(
                        guild -> guild.getEnemies().isEmpty()
                                ? messages.enemiesNoValue
                                : Joiner.on(", ").join(GuildUtils.getTags(guild.getEnemies())),
                        () -> messages.enemiesNoValue));

        GUILD_MEMBERS_COLOR_CONTEXT = new Placeholders<Pair<String, Guild>, Placeholder<Pair<String, Guild>>>()
                .property("members", pair -> {
                    String text = Joiner.on(", ").join(UserUtils.getOnlineNames(pair.getSecond().getMembers())).toString();

                    return !text.contains("<online>")
                            ? text
                            : ONLINE.toFormatter(pair.getFirst()).format(text);
                });
    }

}

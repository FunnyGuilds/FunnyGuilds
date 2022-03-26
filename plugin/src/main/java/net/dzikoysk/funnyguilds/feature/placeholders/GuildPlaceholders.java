package net.dzikoysk.funnyguilds.feature.placeholders;

import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.Entity;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.placeholders.placeholder.FallbackPlaceholder;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.MonoResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.PairResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.SimpleResolver;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildRank;
import net.dzikoysk.funnyguilds.guild.GuildRankManager;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.rank.DefaultTops;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import net.dzikoysk.funnyguilds.user.UserUtils;
import panda.std.Pair;
import panda.utilities.StringUtils;
import panda.utilities.text.Joiner;

public class GuildPlaceholders extends Placeholders<Guild, GuildPlaceholders> {

    public static final GuildPlaceholders GUILD;
    public static final GuildPlaceholders GUILD_ALL;
    public static final GuildPlaceholders GUILD_ALLIES_ENEMIES_ALL;

    public static final SimplePlaceholders<Pair<String, Guild>> GUILD_MEMBERS_COLOR_CONTEXT;

    static {
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        PluginConfiguration config = plugin.getPluginConfiguration();
        MessageConfiguration messages = plugin.getMessageConfiguration();
        GuildRankManager guildRankManager = plugin.getGuildRankManager();

        BiFunction<Collection<String>, String, Object> joinOrDefault = (list, listNoValue) -> list.isEmpty()
                ? listNoValue
                : Joiner.on(", ")
                .join(list);

        Function<Guild, Object> guildProtection = guild -> {
            long now = System.currentTimeMillis();
            long protectionEndTime = guild.getProtection();

            return protectionEndTime < now ? "Brak" : TimeUtils.getDurationBreakdown(protectionEndTime - now);
        };

        GUILD = new GuildPlaceholders()
                .property("name", Guild::getName, () -> messages.gNameNoValue)
                .property("guild", Guild::getName, () -> messages.gNameNoValue)
                .property("tag", Guild::getTag, () -> messages.gTagNoValue);

        GUILD_ALL = new GuildPlaceholders()
                .property("name", Guild::getName, () -> messages.gNameNoValue)
                .property("guild", Guild::getName, () -> messages.gNameNoValue)
                .property("tag", Guild::getTag, () -> messages.gTagNoValue)
                .property("validity",
                        guild -> messages.dateFormat.format(new Date(guild.getValidity())),
                        () -> messages.gValidityNoValue)
                .property("protection", guildProtection::apply, () -> "Brak")
                .property("guild-protection", guildProtection::apply, () -> "Brak")
                .property("owner", guild -> guild.getOwner().getName(), () -> messages.gOwnerNoValue)
                .property("deputies",
                        guild -> joinOrDefault.apply(Entity.names(guild.getDeputies()), messages.gDeputiesNoValue),
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

        GUILD_ALLIES_ENEMIES_ALL = new GuildPlaceholders()
                .property("allies",
                        guild -> joinOrDefault.apply(Entity.names(guild.getAllies()), messages.alliesNoValue),
                        () -> messages.alliesNoValue)
                .property("allies-tags",
                        guild -> joinOrDefault.apply(GuildUtils.getTags(guild.getAllies()), messages.alliesNoValue),
                        () -> messages.alliesNoValue)
                .property("enemies",
                        guild -> joinOrDefault.apply(Entity.names(guild.getEnemies()), messages.enemiesNoValue),
                        () -> messages.enemiesNoValue)
                .property("enemies-tags",
                        guild -> joinOrDefault.apply(GuildUtils.getTags(guild.getEnemies()), messages.enemiesNoValue),
                        () -> messages.enemiesNoValue);

        GUILD_MEMBERS_COLOR_CONTEXT = new SimplePlaceholders<Pair<String, Guild>>()
                .property("members", pair -> {
                    String text = Joiner.on(", ").join(UserUtils.getOnlineNames(pair.getSecond().getMembers())).toString();

                    return !text.contains("<online>")
                            ? text
                            : SimplePlaceholders.ONLINE.toFormatter(pair.getFirst()).format(text);
                });
    }

    public GuildPlaceholders property(String name, MonoResolver<Guild> resolver, SimpleResolver fallbackResolver) {
        return this.property(name, new FallbackPlaceholder<>(resolver, fallbackResolver));
    }

    public GuildPlaceholders property(String name, PairResolver<Guild, GuildRank> resolver, SimpleResolver fallbackResolver) {
        return this.property(name, guild -> resolver.resolve(guild, guild.getRank()), fallbackResolver);
    }

    @Override
    public GuildPlaceholders create() {
        return new GuildPlaceholders();
    }

}

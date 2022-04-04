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

    private static GuildPlaceholders GUILD;
    private static GuildPlaceholders GUILD_ALL;
    private static GuildPlaceholders GUILD_ALLIES_ENEMIES;

    private static SimplePlaceholders<Pair<String, Guild>> GUILD_MEMBERS_COLOR_CONTEXT;

    private static final BiFunction<Collection<String>, String, String> JOIN_OR_DEFAULT = (list, listNoValue) -> list.isEmpty()
            ? listNoValue
            : Joiner.on(", ").join(list).toString();

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

    public static GuildPlaceholders getOrInstallSimplePlaceholders(FunnyGuilds plugin) {
        if (GUILD == null) {
            MessageConfiguration messages = plugin.getMessageConfiguration();

            GUILD = new GuildPlaceholders()
                    .property("name", Guild::getName, () -> messages.gNameNoValue)
                    .property("guild", Guild::getName, () -> messages.gNameNoValue)
                    .property("tag", Guild::getTag, () -> messages.gTagNoValue);
        }
        return GUILD;
    }

    public static GuildPlaceholders getOrInstallAllPlaceholders(FunnyGuilds plugin) {
        if (GUILD_ALL == null) {
            PluginConfiguration config = plugin.getPluginConfiguration();
            MessageConfiguration messages = plugin.getMessageConfiguration();
            GuildRankManager guildRankManager = plugin.getGuildRankManager();

            Function<Guild, Object> guildProtection = guild -> {
                long now = System.currentTimeMillis();
                long protectionEndTime = guild.getProtection();

                return protectionEndTime < now
                        ? "Brak"
                        : TimeUtils.getDurationBreakdown(protectionEndTime - now);
            };

            GUILD_ALL = getOrInstallSimplePlaceholders(plugin)
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
        return GUILD_ALL;
    }

    public static GuildPlaceholders getOrInstallAlliesEnemies(FunnyGuilds plugin) {
        if (GUILD_ALLIES_ENEMIES == null) {
            MessageConfiguration messages = plugin.getMessageConfiguration();
            GUILD_ALLIES_ENEMIES = new GuildPlaceholders()
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
        return GUILD_ALLIES_ENEMIES;
    }

    public static SimplePlaceholders<Pair<String, Guild>> getOrInstallGuildMembers(FunnyGuilds plugin) {
        if (GUILD_MEMBERS_COLOR_CONTEXT == null) {
            MessageConfiguration messages = plugin.getMessageConfiguration();
            GUILD_MEMBERS_COLOR_CONTEXT = new SimplePlaceholders<Pair<String, Guild>>()
                    .property("members", pair -> {
                        String text = JOIN_OR_DEFAULT.apply(UserUtils.getOnlineNames(pair.getSecond().getMembers()), "");

                        return !text.contains("<online>")
                                ? text
                                : SimplePlaceholders.getOrInstallOnlinePlaceholders().toFormatter(pair.getFirst()).format(text);
                    });
        }
        return GUILD_MEMBERS_COLOR_CONTEXT;
    }

}

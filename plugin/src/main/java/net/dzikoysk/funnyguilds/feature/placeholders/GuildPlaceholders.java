package net.dzikoysk.funnyguilds.feature.placeholders;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.function.Supplier;
import net.dzikoysk.funnyguilds.Entity;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.placeholders.impl.GuildPlaceholder;
import net.dzikoysk.funnyguilds.feature.placeholders.impl.Placeholder;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.MonoResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.PairResolver;
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

public class GuildPlaceholders extends Placeholders<Guild, GuildPlaceholder> {

    public static final GuildPlaceholders GUILD;
    public static final GuildPlaceholders GUILD_ALL;
    public static final GuildPlaceholders GUILD_ALLIES_ENEMIES_ALL;

    public static final Placeholders<Pair<String, Guild>, Placeholder<Pair<String, Guild>>> GUILD_MEMBERS_COLOR_CONTEXT;

    static {
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        PluginConfiguration config = plugin.getPluginConfiguration();
        MessageConfiguration messages = plugin.getMessageConfiguration();
        GuildRankManager guildRankManager = plugin.getGuildRankManager();

        GUILD = new GuildPlaceholders()
                .guildProperty(Arrays.asList("name", "guild"), Guild::getName, () -> messages.gNameNoValue)
                .guildProperty("tag", Guild::getTag, () -> messages.gTagNoValue);

        GUILD_ALL = new GuildPlaceholders()
                .guildProperty(Arrays.asList("name", "guild"), Guild::getName, () -> messages.gNameNoValue)
                .guildProperty("tag", Guild::getTag, () -> messages.gTagNoValue)
                .guildProperty("validity",
                        guild -> messages.dateFormat.format(new Date(guild.getValidity())),
                        () -> messages.gValidityNoValue)
                .guildProperty(Arrays.asList("protection", "guild-protection"),
                        guild -> {
                            long now = System.currentTimeMillis();
                            long protectionEndTime = guild.getProtection();

                            return protectionEndTime < now ? "Brak" : TimeUtils.getDurationBreakdown(protectionEndTime - now);
                        }, () -> "Brak")
                .guildProperty("owner", guild -> guild.getOwner().getName(), () -> messages.gOwnerNoValue)
                .guildProperty("deputies", guild ->
                                guild.getDeputies().isEmpty()
                                        ? messages.gDeputiesNoValue
                                        : Joiner.on(", ").join(Entity.names(guild.getDeputies())),
                        () -> messages.gDeputiesNoValue)
                .guildProperty("deputy",
                        guild -> guild.getDeputies().isEmpty()
                                ? messages.gDeputiesNoValue
                                : guild.getDeputies().iterator().next().getName(),
                        () -> messages.gDeputiesNoValue)
                .guildProperty("members-online", guild -> guild.getOnlineMembers().size(), () -> 0)
                .guildProperty("members-all", guild -> guild.getMembers().size(), () -> 0)
                .guildProperty("allies-all", guild -> guild.getAllies().size(), () -> 0)
                .guildProperty("enemies-all", guild -> guild.getEnemies().size(), () -> 0)
                .guildProperty("region-size",
                        guild -> guild.getRegion()
                                .map(Region::getSize)
                                .map(value -> Integer.toString(value))
                                .orElseGet(messages.gRegionSizeNoValue),
                        () -> messages.gRegionSizeNoValue)
                .guildProperty("lives", Guild::getLives, () -> 0)
                .guildProperty("lives-symbol",
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
                .guildProperty("lives-symbol-all",
                        guild -> StringUtils.repeated(guild.getLives(), config.livesRepeatingSymbol.full.getValue()),
                        () -> messages.livesNoValue)
                .guildRankProperty(Arrays.asList("position", "rank"),
                        (guild, rank) -> guildRankManager.isRankedGuild(guild)
                                ? String.valueOf(rank.getPosition(DefaultTops.GUILD_AVG_POINTS_TOP))
                                : messages.minMembersToIncludeNoValue,
                        () -> messages.minMembersToIncludeNoValue)
                .guildRankProperty("total-points", (guild, rank) -> rank.getPoints(), () -> 0)
                .guildRankProperty(Arrays.asList("avg-points", "points"), (guild, rank) -> rank.getAveragePoints(), () -> 0)
                .guildRankProperty("points-format",
                        (guild, rank) -> NumberRange.inRangeToString(rank.getAveragePoints(), config.pointsFormat)
                                .replace("{POINTS}", String.valueOf(guild.getRank().getAveragePoints())),
                        () -> NumberRange.inRangeToString(0, config.pointsFormat)
                                .replace("{POINTS}", "0"))
                .guildRankProperty("kills", (guild, rank) -> rank.getKills(), () -> 0)
                .guildRankProperty("avg-kills", (guild, rank) -> rank.getAverageKills(), () -> 0)
                .guildRankProperty("deaths", (guild, rank) -> rank.getDeaths(), () -> 0)
                .guildRankProperty("avg-deaths", (guild, rank) -> rank.getAverageDeaths(), () -> 0)
                .guildRankProperty("assists", (guild, rank) -> rank.getAssists(), () -> 0)
                .guildRankProperty("avg-assists", (guild, rank) -> rank.getAverageAssists(), () -> 0)
                .guildRankProperty("logouts", (guild, rank) -> rank.getLogouts(), () -> 0)
                .guildRankProperty("avg-logouts", (guild, rank) -> rank.getAverageLogouts(), () -> 0)
                .guildRankProperty("kdr", (guild, rank) -> String.format(Locale.US, "%.2f", rank.getKDR()), () -> 0.0)
                .guildRankProperty("avg-kdr", (guild, rank) -> String.format(Locale.US, "%.2f", rank.getAverageKDR()), () -> 0.0);

        GUILD_ALLIES_ENEMIES_ALL = new GuildPlaceholders()
                .guildProperty("allies",
                        guild -> guild.getAllies().isEmpty()
                                ? messages.alliesNoValue
                                : Joiner.on(", ").join(Entity.names(guild.getAllies())),
                        () -> messages.alliesNoValue)
                .guildProperty("allies-tags",
                        guild -> guild.getAllies().isEmpty()
                                ? messages.alliesNoValue
                                : Joiner.on(", ").join(GuildUtils.getTags(guild.getAllies())),
                        () -> messages.alliesNoValue)
                .guildProperty("enemies",
                        guild -> guild.getEnemies().isEmpty()
                                ? messages.enemiesNoValue
                                : Joiner.on(", ").join(Entity.names(guild.getEnemies())),
                        () -> messages.enemiesNoValue)
                .guildProperty("enemies-tags",
                        guild -> guild.getEnemies().isEmpty()
                                ? messages.enemiesNoValue
                                : Joiner.on(", ").join(GuildUtils.getTags(guild.getEnemies())),
                        () -> messages.enemiesNoValue);

        GUILD_MEMBERS_COLOR_CONTEXT = new Placeholders<Pair<String, Guild>, Placeholder<Pair<String, Guild>>>()
                .property("members", pair -> {
                    String text = Joiner.on(", ").join(UserUtils.getOnlineNames(pair.getSecond().getMembers())).toString();

                    return !text.contains("<online>")
                            ? text
                            : ONLINE.toFormatter(pair.getFirst()).format(text);
                });
    }

    protected GuildPlaceholders guildProperty(String name, MonoResolver<Guild> resolver, Supplier<Object> fallbackSupplier) {
        this.property(name, new GuildPlaceholder(resolver, fallbackSupplier));
        return this;
    }

    protected GuildPlaceholders guildProperty(Collection<String> names, MonoResolver<Guild> resolver, Supplier<Object> fallbackSupplier) {
        names.forEach(name -> this.guildProperty(name, resolver, fallbackSupplier));
        return this;
    }

    protected GuildPlaceholders guildRankProperty(String name, PairResolver<Guild, GuildRank> resolver, Supplier<Object> fallbackSupplier) {
        this.property(name, new GuildPlaceholder((guild) -> resolver.resolve(guild, guild.getRank()), fallbackSupplier));
        return this;
    }

    protected GuildPlaceholders guildRankProperty(Collection<String> names, PairResolver<Guild, GuildRank> resolver, Supplier<Object> fallbackSupplier) {
        names.forEach(name -> this.guildRankProperty(name, resolver, fallbackSupplier));
        return this;
    }

}

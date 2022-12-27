package net.dzikoysk.funnyguilds.guild.placeholders;

import java.util.Locale;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.Entity;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.placeholders.AbstractPlaceholdersService;
import net.dzikoysk.funnyguilds.feature.placeholders.BasicPlaceholders;
import net.dzikoysk.funnyguilds.feature.placeholders.BasicPlaceholdersService;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildRankManager;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.rank.DefaultTops;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.user.UserUtils;
import panda.std.Option;
import panda.std.Pair;
import panda.utilities.StringUtils;

public class GuildPlaceholdersService extends AbstractPlaceholdersService<Guild, GuildPlaceholders> {

    public static final BasicPlaceholders<Pair<String, Guild>> GUILD_MEMBERS_COLOR_CONTEXT = new BasicPlaceholders<Pair<String, Guild>>()
            .property("members", pair -> {
                String text = JOIN_OR_DEFAULT.apply(UserUtils.getOnlineNames(pair.getSecond().getMembers()), "");

                return !text.contains("<online>")
                        ? text
                        : BasicPlaceholdersService.ONLINE.toFormatter(pair.getFirst()).format(text);
            });

    private static Option<GuildPlaceholders> SIMPLE = Option.none();

    @Override
    public String format(String text, Guild guild) {
        text = super.format(text, guild);
        text = GUILD_MEMBERS_COLOR_CONTEXT.toVariablesFormatter(Pair.of(ChatUtils.getLastColorBefore(text, "{MEMBERS}"), guild))
                .format(text);
        return text;
    }

    public static Option<GuildPlaceholders> getSimplePlaceholders() {
        return SIMPLE;
    }

    public static GuildPlaceholders createSimplePlaceholders(FunnyGuilds plugin) {
        MessageConfiguration messages = plugin.getMessageConfiguration();

        GuildPlaceholders placeholders = new GuildPlaceholders()
                .property("name", Guild::getName, () -> messages.gNameNoValue)
                .property("guild", Guild::getName, () -> messages.gNameNoValue)
                .property("tag", Guild::getTag, () -> messages.gTagNoValue);
        SIMPLE = Option.of(placeholders);

        return placeholders;
    }

    public static GuildPlaceholders createGuildPlaceholders(FunnyGuilds plugin) {
        PluginConfiguration config = plugin.getPluginConfiguration();
        MessageConfiguration messages = plugin.getMessageConfiguration();
        GuildRankManager rankManager = plugin.getGuildRankManager();

        return new GuildPlaceholders()
                .property("validity", guild -> messages.dateFormat.format(guild.getValidity()), () -> messages.gValidityNoValue)
                .property("validity-time", guild -> formatTime(guild, Guild::getValidity), () -> messages.gValidityNoValue)
                .property("protection", guild -> messages.dateFormat.format(guild.getProtection()), () -> messages.gProtectionNoValue)
                .property("protection-time", guild -> formatTime(guild, Guild::getProtection), () -> messages.gProtectionNoValue)
                .property("owner", guild -> guild.getOwner().getName(), () -> messages.gOwnerNoValue)
                .property("deputies",
                        guild -> JOIN_OR_DEFAULT.apply(Entity.names(guild.getDeputies()), messages.gDeputiesNoValue),
                        () -> messages.gDeputiesNoValue)
                .property("deputy",
                        guild -> guild.getDeputies().isEmpty()
                                ? messages.gDeputyNoValue
                                : guild.getDeputies().iterator().next().getName(),
                        () -> messages.gDeputyNoValue)
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
                .property("pvp",
                        guild -> guild.hasPvPEnabled()
                                ? messages.pvpStatusOn
                                : messages.pvpStatusOff,
                        () -> messages.pvpStatusOff)
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
                        (guild, rank) -> rankManager.isRankedGuild(guild)
                                ? String.valueOf(rank.getPosition(DefaultTops.GUILD_AVG_POINTS_TOP))
                                : messages.minMembersToIncludeNoValue,
                        () -> messages.minMembersToIncludeNoValue)
                .property("rank",
                        (guild, rank) -> rankManager.isRankedGuild(guild)
                                ? String.valueOf(rank.getPosition(DefaultTops.GUILD_AVG_POINTS_TOP))
                                : messages.minMembersToIncludeNoValue,
                        () -> messages.minMembersToIncludeNoValue)
                //TODO total-points -> points (I know this will break up backwards compatibility so I'm not doing it now)
                .property("total-points", (guild, rank) -> rank.getPoints(), () -> 0)
                .property("avg-points", (guild, rank) -> rank.getAveragePoints(), () -> 0)
                .property("points", (guild, rank) -> rank.getAveragePoints(), () -> 0)
                .property("points-format",
                        (guild, rank) -> FunnyFormatter.format(NumberRange.inRangeToString(rank.getAveragePoints(),
                                config.pointsFormat), "{POINTS}", guild.getRank().getAveragePoints()),
                        () -> FunnyFormatter.format(NumberRange.inRangeToString(0, config.pointsFormat), "{POINTS}", 0))
                .property("kills", (guild, rank) -> rank.getKills(), () -> 0)
                .property("avg-kills", (guild, rank) -> rank.getAverageKills(), () -> 0)
                .property("deaths", (guild, rank) -> rank.getDeaths(), () -> 0)
                .property("avg-deaths", (guild, rank) -> rank.getAverageDeaths(), () -> 0)
                .property("assists", (guild, rank) -> rank.getAssists(), () -> 0)
                .property("avg-assists", (guild, rank) -> rank.getAverageAssists(), () -> 0)
                .property("logouts", (guild, rank) -> rank.getLogouts(), () -> 0)
                .property("avg-logouts", (guild, rank) -> rank.getAverageLogouts(), () -> 0)
                .property("kdr", (guild, rank) -> String.format(Locale.US, "%.2f", rank.getKDR()), () -> 0.0)
                .property("avg-kdr", (guild, rank) -> String.format(Locale.US, "%.2f", rank.getAverageKDR()), () -> 0.0)
                .property("kda", (guild, rank) -> String.format(Locale.US, "%.2f", rank.getKDA()), () -> 0.0)
                .property("avg-kda", (guild, rank) -> String.format(Locale.US, "%.2f", rank.getAverageKDA()), () -> 0.0);
    }

    public static GuildPlaceholders createAlliesEnemiesPlaceholders(FunnyGuilds plugin) {
        MessageConfiguration messages = plugin.getMessageConfiguration();
        return new GuildPlaceholders()
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

    private static String formatTime(Guild guild, Function<Guild, Long> timeFunction) {
        long now = System.currentTimeMillis();
        long endTime = timeFunction.apply(guild);

        return endTime < now
                ? "Brak"
                : TimeUtils.getDurationBreakdown(endTime - now);
    }

}

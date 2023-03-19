package net.dzikoysk.funnyguilds.guild.placeholders;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.dzikoysk.funnyguilds.Entity;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.feature.placeholders.AbstractPlaceholdersService;
import net.dzikoysk.funnyguilds.feature.placeholders.BasicPlaceholders;
import net.dzikoysk.funnyguilds.feature.placeholders.BasicPlaceholdersService;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildRankManager;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.user.UserUtils;
import org.bukkit.ChatColor;
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
        MessageService messages = plugin.getMessageService();

        GuildPlaceholders placeholders = new GuildPlaceholders()
                .property("name", Guild::getName, () -> messages.get(config -> config.gNameNoValue))
                .property("guild", Guild::getName, () -> messages.get(config -> config.gNameNoValue))
                .property("tag", Guild::getTag, () -> messages.get(config -> config.gTagNoValue));
        SIMPLE = Option.of(placeholders);

        return placeholders;
    }

    public static GuildPlaceholders createGuildPlaceholders(FunnyGuilds plugin) {
        PluginConfiguration pluginConfiguration = plugin.getPluginConfiguration();
        MessageService messages = plugin.getMessageService();
        GuildRankManager rankManager = plugin.getGuildRankManager();

        return new GuildPlaceholders()
                .property("owner", guild -> guild.getOwner().getName(), () -> messages.get(config -> config.gOwnerNoValue))
                .property("deputies",
                        guild -> JOIN_OR_DEFAULT.apply(Entity.names(guild.getDeputies()), messages.get(config -> config.gDeputiesNoValue)),
                        () -> messages.get(config -> config.gDeputiesNoValue))
                .property("deputy",
                        guild -> guild.getDeputies().isEmpty()
                                ? messages.get(config -> config.gDeputyNoValue)
                                : guild.getDeputies().iterator().next().getName(),
                        () -> messages.get(config -> config.gDeputyNoValue))
                .property("members-online", guild -> guild.getOnlineMembers().size(), () -> 0)
                .property("members-all", guild -> guild.getMembers().size(), () -> 0)
                .property("allies-all", guild -> guild.getAllies().size(), () -> 0)
                .property("enemies-all", guild -> guild.getEnemies().size(), () -> 0)
                .property("region-size",
                        guild -> guild.getRegion()
                                .map(Region::getSize)
                                .map(value -> Integer.toString(value))
                                .orElseGet((String) messages.get(config -> config.gRegionSizeNoValue)),
                        () -> messages.get(config -> config.gRegionSizeNoValue))
                .property("pvp",
                        guild -> guild.hasPvPEnabled()
                                ? messages.get(config -> config.pvpStatusOn)
                                : messages.get(config -> config.pvpStatusOff),
                        () -> messages.get(config -> config.pvpStatusOff))
                .timeProperty("validity", Guild::getValidity, messages, config -> config.gValidityNoValue)
                .timeProperty("protection", Guild::getProtection, messages, config -> config.gProtectionNoValue)
                .property("lives", Guild::getLives, () -> 0)
                .property("lives-symbol",
                        guild -> {
                            int lives = guild.getLives();
                            if (lives <= pluginConfiguration.warLives) {
                                return StringUtils.repeated(lives, pluginConfiguration.livesRepeatingSymbol.full.getValue()) +
                                        StringUtils.repeated(pluginConfiguration.warLives - lives, pluginConfiguration.livesRepeatingSymbol.empty.getValue());
                            }
                            else {
                                return StringUtils.repeated(pluginConfiguration.warLives, pluginConfiguration.livesRepeatingSymbol.full.getValue()) +
                                        pluginConfiguration.livesRepeatingSymbol.more.getValue();
                            }
                        }, () -> messages.get(config -> config.livesNoValue))
                .property("lives-symbol-all",
                        guild -> StringUtils.repeated(guild.getLives(), pluginConfiguration.livesRepeatingSymbol.full.getValue()),
                        () -> messages.get(config -> config.livesNoValue))
                .property("points", (guild, rank) -> rank.getPoints(), () -> 0)
                .property("avg-points", (guild, rank) -> rank.getAveragePoints(), () -> 0)
                .property("avg-points-format",
                        (guild, rank) -> FunnyFormatter.format(NumberRange.inRangeToString(rank.getAveragePoints(),
                                pluginConfiguration.pointsFormat), "{POINTS}", guild.getRank().getAveragePoints()),
                        () -> FunnyFormatter.format(NumberRange.inRangeToString(0, pluginConfiguration.pointsFormat), "{POINTS}", 0))
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
        MessageService messages = plugin.getMessageService();
        return new GuildPlaceholders()
                .property("allies",
                        guild -> JOIN_OR_DEFAULT.apply(Entity.names(guild.getAllies()), messages.get(config -> config.alliesNoValue)),
                        () -> messages.get(config -> config.alliesNoValue))
                .property("allies-tags",
                        guild -> JOIN_OR_DEFAULT.apply(GuildUtils.getTags(guild.getAllies()), messages.get(config -> config.alliesNoValue)),
                        () -> messages.get(config -> config.alliesNoValue))
                .property("enemies",
                        guild -> JOIN_OR_DEFAULT.apply(Entity.names(guild.getEnemies()), messages.get(config -> config.enemiesNoValue)),
                        () -> messages.get(config -> config.enemiesNoValue))
                .property("enemies-tags",
                        guild -> JOIN_OR_DEFAULT.apply(GuildUtils.getTags(guild.getEnemies()), messages.get(config -> config.enemiesNoValue)),
                        () -> messages.get(config -> config.enemiesNoValue));
    }

    @Override
    public List<FunnyFormatter> getFormatters(Guild data) {
        List<FunnyFormatter> formatters = new ArrayList<>(super.getFormatters(data));
        formatters.add(GUILD_MEMBERS_COLOR_CONTEXT.toVariablesFormatter(Pair.of(ChatColor.RESET.toString(), data)));
        return formatters;
    }

}

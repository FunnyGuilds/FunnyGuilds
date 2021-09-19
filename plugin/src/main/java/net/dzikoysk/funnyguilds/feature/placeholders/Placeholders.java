package net.dzikoysk.funnyguilds.feature.placeholders;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.IntegerRange;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import net.dzikoysk.funnyguilds.user.UserUtils;
import org.bukkit.ChatColor;
import panda.utilities.text.Formatter;
import panda.utilities.text.Joiner;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class Placeholders<T> {

    public static final Placeholders<String> ONLINE;
    public static final Placeholders<Guild> GUILD;
    public static final Placeholders<Guild> GUILD_ALL;

    static {
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        MessageConfiguration messages = plugin.getMessageConfiguration();
        PluginConfiguration config = plugin.getPluginConfiguration();

        ONLINE = new Placeholders<String>()
                .bind("<online>",  () -> ChatColor.GREEN)
                .bind("</online>", end -> end);

        GUILD = new Placeholders<Guild>()
                .bracket("GUILD", Guild::getName)
                .bracket("TAG",   Guild::getTag);

        Function<Guild, Object> bindGuildProtection = guild -> {
            long now = System.currentTimeMillis();
            long protectionEndTime = guild.getProtection();

            return protectionEndTime < now ? "Brak" : TimeUtils.getDurationBreakdown(protectionEndTime - now);
        };

        BiFunction<Collection<String>, String, Object> joinOrDefault = (list, defaultValue) -> list.isEmpty() ? defaultValue : Joiner.on(", ").join(list);

        GUILD_ALL = new Placeholders<Guild>()
                .bracket("GUILD",               Guild::getName)
                .bracket("TAG",                 Guild::getTag)
                .bracket("OWNER",               guild -> guild.getOwner().getName())
                .bracket("MEMBERS-ONLINE",      guild -> guild.getOnlineMembers().size())
                .bracket("MEMBERS-ALL",         guild -> guild.getMembers().size())
                .bracket("MEMBERS",             guild -> {
                    String text = Joiner.on(", ").join(UserUtils.getOnlineNames(guild.getMembers())).toString();

                    return ONLINE
                            .toFormatter(ChatColor.getLastColors(text.split("<online>")[0]))
                            .format(text);
                })
                .bracket("DEPUTIES",            guild -> joinOrDefault.apply(UserUtils.getNamesOfUsers(guild.getDeputies()), "Brak"))
                .bracket("REGION-SIZE",         guild -> config.regionsEnabled ? guild.getRegion().getSize() : messages.gRegionSizeNoValue)
                .bracket("GUILD-PROTECTION",    bindGuildProtection)
                .bracket("POINTS-FORMAT",       guild -> IntegerRange.inRangeToString(guild.getRank().getAveragePoints(), config.pointsFormat))
                .bracket("POINTS",              guild -> guild.getRank().getAveragePoints())
                .bracket("KILLS",               guild -> guild.getRank().getKills())
                .bracket("DEATHS",              guild -> guild.getRank().getDeaths())
                .bracket("ASSISTS",             guild -> guild.getRank().getAssists())
                .bracket("LOGOUTS",             guild -> guild.getRank().getLogouts())
                .bracket("KDR",                 guild -> String.format(Locale.US, "%.2f", guild.getRank().getKDR()))
                .bracket("VALIDITY",            guild -> config.dateFormat.format(new Date(guild.getValidity())))
                .bracket("LIVES",               Guild::getLives)
                .bracket("RANK",                guild -> guild.getMembers().size() >= config.minMembersToInclude
                        ? guild.getRank().getPosition()
                        : messages.minMembersToIncludeNoValue)
                .bracket("ALLIES",       guild -> joinOrDefault.apply(GuildUtils.getNames(guild.getAllies()), messages.alliesNoValue))
                .bracket("ALLIES-TAGS",  guild -> joinOrDefault.apply(GuildUtils.getTags(guild.getAllies()), messages.alliesNoValue))
                .bracket("ENEMIES",      guild -> joinOrDefault.apply(GuildUtils.getNames(guild.getEnemies()), messages.enemiesNoValue))
                .bracket("ENEMIES-TAGS", guild -> joinOrDefault.apply(GuildUtils.getTags(guild.getEnemies()), messages.enemiesNoValue));
    }

    private final Map<String, Function<T, String>> placeholders = new HashMap<>();

    public Placeholders() {
    }

    public Placeholders<T> bind(String key, Function<T, Object> bind) {
        return Placeholders.of(placeholders, key, t -> String.valueOf(bind.apply(t)));
    }

    public Placeholders<T> bind(String key, Supplier<Object> bind) {
        return Placeholders.of(placeholders, key, t -> String.valueOf(bind.get()));
    }

    public Placeholders<T> bracket(String key, Function<T, Object> bind) {
        return bind("{" + key + "}", bind);
    }

    public Placeholders<T> bracket(String key, Supplier<Object> bind) {
        return bind("{" + key + "}", bind);
    }

    public String format(String text, T data) {
        for (Map.Entry<String, Function<T, String>> entry : placeholders.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue().apply(data));
        }

        return text;
    }

    public Formatter toFormatter(T data) {
        Formatter formatter = new Formatter();

        placeholders.forEach((key, bind) -> formatter.register(key, bind.apply(data)));

        return formatter;
    }

    private static <T> Placeholders<T> of(Map<String, Function<T, String>> placeholdersMap, String newKey, Function<T, String> newBind) {
        Placeholders<T> placeholders = new Placeholders<>();

        placeholders.placeholders.putAll(placeholdersMap);
        placeholders.placeholders.put(newKey, newBind);

        return placeholders;
    }

}

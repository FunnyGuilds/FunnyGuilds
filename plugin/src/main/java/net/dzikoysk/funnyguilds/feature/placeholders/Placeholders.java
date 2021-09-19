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
                .raw("<online>",  () -> ChatColor.GREEN)
                .raw("</online>", end -> end);

        GUILD = new Placeholders<Guild>()
                .property("GUILD", Guild::getName)
                .property("TAG",   Guild::getTag);

        Function<Guild, Object> bindGuildProtection = guild -> {
            long now = System.currentTimeMillis();
            long protectionEndTime = guild.getProtection();

            return protectionEndTime < now ? "Brak" : TimeUtils.getDurationBreakdown(protectionEndTime - now);
        };

        BiFunction<Collection<String>, String, Object> joinOrDefault = (list, listNoValue) -> list.isEmpty() ? listNoValue : Joiner.on(", ").join(list);

        GUILD_ALL = new Placeholders<Guild>()
                .property("GUILD",               Guild::getName)
                .property("TAG",                 Guild::getTag)
                .property("OWNER", guild -> guild.getOwner().getName())
                .property("MEMBERS-ONLINE", guild -> guild.getOnlineMembers().size())
                .property("MEMBERS-ALL", guild -> guild.getMembers().size())
                .property("MEMBERS", guild -> {
                    String text = Joiner.on(", ").join(UserUtils.getOnlineNames(guild.getMembers())).toString();

                    return ONLINE
                            .toFormatter(ChatColor.getLastColors(text.split("<online>")[0]))
                            .format(text);
                })
                .property("DEPUTIES", guild -> joinOrDefault.apply(UserUtils.getNamesOfUsers(guild.getDeputies()), "Brak"))
                .property("REGION-SIZE", guild -> config.regionsEnabled ? guild.getRegion().getSize() : messages.gRegionSizeNoValue)
                .property("GUILD-PROTECTION",    bindGuildProtection)
                .property("POINTS-FORMAT", guild -> IntegerRange.inRangeToString(guild.getRank().getAveragePoints(), config.pointsFormat))
                .property("POINTS", guild -> guild.getRank().getAveragePoints())
                .property("KILLS", guild -> guild.getRank().getKills())
                .property("DEATHS", guild -> guild.getRank().getDeaths())
                .property("ASSISTS", guild -> guild.getRank().getAssists())
                .property("LOGOUTS", guild -> guild.getRank().getLogouts())
                .property("KDR", guild -> String.format(Locale.US, "%.2f", guild.getRank().getKDR()))
                .property("VALIDITY", guild -> config.dateFormat.format(new Date(guild.getValidity())))
                .property("LIVES",               Guild::getLives)
                .property("RANK", guild -> guild.getMembers().size() >= config.minMembersToInclude
                        ? guild.getRank().getPosition()
                        : messages.minMembersToIncludeNoValue)
                .property("ALLIES", guild -> joinOrDefault.apply(GuildUtils.getNames(guild.getAllies()), messages.alliesNoValue))
                .property("ALLIES-TAGS", guild -> joinOrDefault.apply(GuildUtils.getTags(guild.getAllies()), messages.alliesNoValue))
                .property("ENEMIES", guild -> joinOrDefault.apply(GuildUtils.getNames(guild.getEnemies()), messages.enemiesNoValue))
                .property("ENEMIES-TAGS", guild -> joinOrDefault.apply(GuildUtils.getTags(guild.getEnemies()), messages.enemiesNoValue));
    }

    private final Map<String, Function<T, String>> placeholders = new HashMap<>();

    public Placeholders() {
    }

    public Placeholders<T> raw(String key, Function<T, Object> bind) {
        return Placeholders.of(placeholders, key, t -> String.valueOf(bind.apply(t)));
    }

    public Placeholders<T> raw(String key, Supplier<Object> bind) {
        return Placeholders.of(placeholders, key, t -> String.valueOf(bind.get()));
    }

    public Placeholders<T> property(String key, Function<T, Object> bind) {
        return raw("{" + key + "}", bind);
    }

    public Placeholders<T> property(String key, Supplier<Object> bind) {
        return raw("{" + key + "}", bind);
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

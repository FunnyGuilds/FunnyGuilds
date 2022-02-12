package net.dzikoysk.funnyguilds.feature.placeholders;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import net.dzikoysk.funnyguilds.Entity;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.IntegerRange;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import net.dzikoysk.funnyguilds.user.UserUtils;
import org.bukkit.ChatColor;
import panda.std.Pair;
import panda.utilities.StringUtils;
import panda.utilities.text.Formatter;
import panda.utilities.text.Joiner;

public class Placeholders<T> {

    public static final Placeholders<String> ONLINE;
    public static final Placeholders<Guild> GUILD;
    public static final Placeholders<Guild> GUILD_ALL;
    public static final Placeholders<Pair<String, Guild>> GUILD_MEMBERS_COLOR_CONTEXT;

    static {
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        MessageConfiguration messages = plugin.getMessageConfiguration();
        PluginConfiguration config = plugin.getPluginConfiguration();

        ONLINE = new Placeholders<String>()
                .raw("<online>", () -> ChatColor.GREEN)
                .raw("</online>", end -> end);

        GUILD = new Placeholders<Guild>()
                .property("GUILD", Guild::getName)
                .property("TAG", Guild::getTag);

        Function<Guild, Object> bindGuildProtection = guild -> {
            long now = System.currentTimeMillis();
            long protectionEndTime = guild.getProtection();

            return protectionEndTime < now ? "Brak" : TimeUtils.getDurationBreakdown(protectionEndTime - now);
        };

        BiFunction<Collection<String>, String, Object> joinOrDefault = (list, listNoValue) -> list.isEmpty() ? listNoValue : Joiner.on(", ").join(list);

        GUILD_ALL = new Placeholders<Guild>()
                .property("GUILD", Guild::getName)
                .property("TAG", Guild::getTag)
                .property("OWNER", guild -> guild.getOwner().getName())
                .property("MEMBERS-ONLINE", guild -> guild.getOnlineMembers().size())
                .property("MEMBERS-ALL", guild -> guild.getMembers().size())
                .property("DEPUTIES", guild -> joinOrDefault.apply(Entity.names(guild.getDeputies()), "Brak"))
                .property("REGION-SIZE", guild -> config.regionsEnabled && guild.hasRegion() ? guild.getRegionOption().get().getSize() : messages.gRegionSizeNoValue)
                .property("GUILD-PROTECTION", bindGuildProtection)
                .property("POINTS-FORMAT", guild -> IntegerRange.inRangeToString(guild.getRank().getAveragePoints(), config.pointsFormat))
                .property("POINTS", guild -> guild.getRank().getAveragePoints())
                .property("KILLS", guild -> guild.getRank().getKills())
                .property("DEATHS", guild -> guild.getRank().getDeaths())
                .property("ASSISTS", guild -> guild.getRank().getAssists())
                .property("LOGOUTS", guild -> guild.getRank().getLogouts())
                .property("KDR", guild -> String.format(Locale.US, "%.2f", guild.getRank().getKDR()))
                .property("VALIDITY", guild -> messages.dateFormat.format(new Date(guild.getValidity())))
                .property("LIVES", Guild::getLives)
                .property("LIVES-SYMBOL", guild -> {
                    int lives = guild.getLives();
                    if (lives <= config.warLives) {
                        return StringUtils.repeated(lives, config.livesRepeatingSymbol.full.getValue()) +
                                StringUtils.repeated(config.warLives - lives, config.livesRepeatingSymbol.empty.getValue());
                    }
                    else {
                        return StringUtils.repeated(config.warLives, config.livesRepeatingSymbol.full.getValue()) +
                                config.livesRepeatingSymbol.more.getValue();
                    }
                })
                .property("LIVES-SYMBOL-ALL", guild -> StringUtils.repeated(guild.getLives(), config.livesRepeatingSymbol.full.getValue()))
                .property("RANK", guild -> guild.isRanked() ? guild.getRank().getPosition() : messages.minMembersToIncludeNoValue)
                .property("ALLIES", guild -> joinOrDefault.apply(Entity.names(guild.getAllies()), messages.alliesNoValue))
                .property("ALLIES-TAGS", guild -> joinOrDefault.apply(GuildUtils.getTags(guild.getAllies()), messages.alliesNoValue))
                .property("ENEMIES", guild -> joinOrDefault.apply(Entity.names(guild.getEnemies()), messages.enemiesNoValue))
                .property("ENEMIES-TAGS", guild -> joinOrDefault.apply(GuildUtils.getTags(guild.getEnemies()), messages.enemiesNoValue));

        GUILD_MEMBERS_COLOR_CONTEXT = new Placeholders<Pair<String, Guild>>()
                .property("MEMBERS", pair -> {
                    String text = Joiner.on(", ").join(UserUtils.getOnlineNames(pair.getSecond().getMembers())).toString();

                    return !text.contains("<online>")
                            ? text
                            : ONLINE.toFormatter(pair.getFirst()).format(text);
                });
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

package net.dzikoysk.funnyguilds.guild;

import java.util.Map;
import java.util.Optional;
import java.util.function.ToIntFunction;
import net.dzikoysk.funnyguilds.rank.StatKey;

/**
 * Guild-owned counters sharing the ranking engine with user statistics. Their values live on {@link Guild}
 * and are changed through its {@code @Internal} war setters (for example {@link Guild#setWarsWon(int)}),
 * not through {@code RankService} user writes, which reject native guild keys for per-user tracking.
 * Those setters do not refresh ranking trackers themselves: the plugin's war flow refreshes them via
 * {@link GuildManager#refreshGuild(Guild)}, and direct counter changes must do the same. Aggregations
 * read the guild's own value through {@link #value(Guild, StatKey)} instead of aggregating over members.
 */
public final class GuildStatKeys {

    public static final StatKey<Integer> WARS_WON = StatKey.integerStatKey("wars_won");
    public static final StatKey<Integer> WARS_LOST = StatKey.integerStatKey("wars_lost");
    public static final StatKey<Integer> WAR_STREAK = StatKey.integerStatKey("war_streak");

    private static final Map<StatKey<?>, ToIntFunction<Guild>> VALUES = Map.of(
            WARS_WON, Guild::getWarsWon, WARS_LOST, Guild::getWarsLost,
            WAR_STREAK, Guild::getWarStreak);

    private GuildStatKeys() {
    }

    /** Whether the key is a native guild-owned counter whose value lives on {@link Guild}. */
    public static boolean isNative(StatKey<?> key) {
        return VALUES.containsKey(key);
    }

    /** The guild's current value for a native key, or empty for any other key. */
    public static Optional<Integer> value(Guild guild, StatKey<?> key) {
        return Optional.ofNullable(VALUES.get(key)).map(reader -> reader.applyAsInt(guild));
    }
}

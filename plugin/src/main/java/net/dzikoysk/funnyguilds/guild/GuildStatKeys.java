package net.dzikoysk.funnyguilds.guild;

import java.util.Map;
import java.util.Optional;
import java.util.function.ToIntFunction;
import net.dzikoysk.funnyguilds.rank.StatKey;

/** Existing guild-owned counters use the same ranking engine as derived member statistics. */
public final class GuildStatKeys {

    public static final StatKey<Integer> WARS_WON = StatKey.integerStatKey("wars_won");
    public static final StatKey<Integer> WARS_LOST = StatKey.integerStatKey("wars_lost");
    public static final StatKey<Integer> WAR_STREAK = StatKey.integerStatKey("war_streak");

    private static final Map<StatKey<?>, ToIntFunction<Guild>> VALUES = Map.of(
            WARS_WON, Guild::getWarsWon, WARS_LOST, Guild::getWarsLost,
            WAR_STREAK, Guild::getWarStreak);

    private GuildStatKeys() {
    }

    public static boolean isNative(StatKey<?> key) {
        return VALUES.containsKey(key);
    }

    public static Optional<Integer> value(Guild guild, StatKey<?> key) {
        return Optional.ofNullable(VALUES.get(key)).map(reader -> reader.applyAsInt(guild));
    }
}

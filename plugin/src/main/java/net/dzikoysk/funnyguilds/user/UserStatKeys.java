package net.dzikoysk.funnyguilds.user;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.dzikoysk.funnyguilds.rank.StatKey;

/**
 * Canonical built-in user statistics. A new user starts with {@link #POINTS} at the configured
 * {@code rank-start} value (floored at zero) and every other counter at zero.
 *
 * <p>{@link #KDR} and {@link #KDA} are read-only ratios derived from kills, deaths and assists and
 * recomputed on every write to those counters; writing them directly is rejected.
 *
 * <p>{@link #VALUES} lists the built-ins excluded from the serialized statistics payload: the counters
 * are persisted in dedicated legacy columns, which stay authoritative, while the ratios are never
 * persisted at all.
 */
public final class UserStatKeys {

    public static final StatKey<Integer> POINTS = StatKey.integerStatKey("points");
    public static final StatKey<Integer> KILLS = StatKey.integerStatKey("kills");
    public static final StatKey<Integer> DEATHS = StatKey.integerStatKey("deaths");
    public static final StatKey<Integer> ASSISTS = StatKey.integerStatKey("assists");
    public static final StatKey<Integer> LOGOUTS = StatKey.integerStatKey("logouts");
    public static final StatKey<Double> KDR = StatKey.doubleStatKey("kdr");
    public static final StatKey<Double> KDA = StatKey.doubleStatKey("kda");

    public static final List<StatKey<?>> VALUES = Collections.unmodifiableList(Arrays.asList(
            POINTS, KILLS, DEATHS, ASSISTS, LOGOUTS, KDR, KDA
    ));


    private UserStatKeys() {
    }
}

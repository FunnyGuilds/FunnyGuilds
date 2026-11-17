package net.dzikoysk.funnyguilds.rank;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildStatKeys;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserStatKeys;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;
import panda.std.function.TriFunction;

/**
 * Strategy for aggregating per-member stat values into a single guild-level value, used as
 * the aggregation axis of rank placeholders and tops.
 *
 * <p>Members with no recorded value for a stat contribute zero to the aggregation, and
 * averages over an empty guild are zero. Value types are limited to {@link Integer},
 * {@link Long} and {@link Double}; {@link Long} exists for aggregate results only - tracked
 * user statistics and persistence stay Integer/Double, so a Long aggregation is readable
 * through guild trackers but has no user-side storage. Identity is the aggregation
 * {@link Key} alone, so keys of custom instances created through {@link #create} must be
 * unique.
 */
public final class GuildStatAggregationType<R extends Number> implements Keyed {

    /**
     * Exact sum of member values as a {@code Long}. Member values are Ints, so the exact sum
     * fits in a Long for any guild size the plugin supports; no saturation or wrapping ever
     * occurs, unlike the Int-typed totals of earlier builds. Reads through {@link RankService}
     * return these totals as exact {@code Long} values.
     */
    public static final GuildStatAggregationType<Long> TOTAL_LONG = new GuildStatAggregationType<>(
            "guild_stat_aggregation_total_long",
            Long.class,
            (controller, guild, key) -> {
                // Member values are Ints, so the exact sum always fits in a Long.
                long total = 0L;
                for (User member : guild.getMembers()) {
                    total += controller.getUserStatValue(
                            member,
                            key
                    ).map(Number::intValue).orElse(0);
                }
                return total;
            }
    );

    /** Sum of member values as doubles. */
    public static final GuildStatAggregationType<Double> TOTAL_DOUBLE = new GuildStatAggregationType<>(
            "guild_stat_aggregation_total_double",
            Double.class,
            (controller, guild, key) -> {
                double total = 0.0;
                for (User member : guild.getMembers()) {
                    total += controller.getUserStatValue(
                            member,
                            key
                    ).map(Number::doubleValue).orElse(0.0);
                }
                return total;
            }
    );

    /** Fractional mean of member values; zero for an empty guild. */
    public static final GuildStatAggregationType<Double> AVERAGE = new GuildStatAggregationType<>(
            "guild_stat_aggregation_average",
            Double.class,
            (controller, guild, key) -> {
                Set<User> members = guild.getMembers();
                int memberCount = members.size();
                if (memberCount == 0) {
                    return 0.0;
                }

                double total = 0.0;
                for (User member : members) {
                    total += controller.getUserStatValue(
                            member,
                            key
                    ).map(Number::doubleValue).orElse(0.0);
                }
                return total / memberCount;
            }
    );

    /**
     * Mean of member values, truncated toward zero to match the legacy integer arithmetic.
     * The sum is accumulated exactly in a {@code long}, so the truncated result always fits
     * in an Int.
     */
    public static final GuildStatAggregationType<Integer> AVERAGE_INT = new GuildStatAggregationType<>(
            "guild_stat_aggregation_average_int", Integer.class,
            (service, guild, key) -> {
                Collection<User> members = guild.getMembers();
                if (members.isEmpty()) {
                    return 0;
                }
                // Divide the exact Long sum; long division truncates toward zero like the legacy Int
                // arithmetic, and the truncated average of Int values never leaves the Int range.
                long total = 0;
                for (User member : members) {
                    total += service.getUserStatValue(
                            member,
                            key
                    ).map(Number::intValue).orElse(0);
                }
                return (int) (total / members.size());
            }
    );

    public static final Collection<GuildStatAggregationType<?>> VALUES = ImmutableList.of(
            TOTAL_LONG,
            TOTAL_DOUBLE,
            AVERAGE,
            AVERAGE_INT
    );

    private final Key key;
    private final Class<R> valueType;
    private final TriFunction<RankService, Guild, StatKey<?>, R> valueComputer;

    private GuildStatAggregationType(
            @KeyPattern String key,
            Class<R> valueType,
            TriFunction<RankService, Guild, StatKey<?>, R> valueComputer
    ) {
        this(Key.key(FunnyGuilds.NAMESPACE, key), valueType, valueComputer);
    }

    private GuildStatAggregationType(Key key, Class<R> valueType, TriFunction<RankService, Guild, StatKey<?>, R> computer) {
        if (valueType != Integer.class && valueType != Long.class && valueType != Double.class) {
            throw new IllegalArgumentException("Unsupported aggregation type " + valueType);
        }
        this.key = Objects.requireNonNull(key, "aggregation key");
        this.valueType = valueType;
        this.valueComputer = Objects.requireNonNull(computer, "aggregation function");
    }

    /**
     * Creates a custom aggregation. Only {@link Integer}, {@link Long} and {@link Double} value
     * types are accepted ({@link Long} is meant for aggregate-only results such as exact totals),
     * and instances are compared by aggregation key alone, so the key must not collide with any
     * existing aggregation.
     */
    public static <R extends Number> GuildStatAggregationType<R> create(
            Key key, Class<R> valueType, TriFunction<RankService, Guild, StatKey<?>, R> computer) {
        return new GuildStatAggregationType<>(key, valueType, computer);
    }

    @Override
    public @NotNull Key key() {
        return this.key;
    }

    public Class<R> getValueType() {
        return this.valueType;
    }

    /**
     * Computes the guild value for {@code key}. Guild-native stats ({@link GuildStatKeys}) are
     * returned in this aggregation's value type and bypass member aggregation entirely.
     * {@code TOTAL_DOUBLE} resolves KDR and KDA from summed raw kills, deaths and assists -
     * accumulated exactly through {@link #TOTAL_LONG} - rather than from the mean of member
     * ratios, so the ratio of sums and the sum of ratios differ by design. Any other stat goes
     * through this aggregation's member aggregation.
     */
    public R computeValue(
            RankService controller,
            Guild guild,
            StatKey<?> key
    ) {
        Optional<Integer> nativeValue = GuildStatKeys.value(guild, key);
        if (nativeValue.isPresent()) {
            int value = nativeValue.orElseThrow();
            if (this.valueType == Integer.class) {
                return this.valueType.cast(value);
            }
            if (this.valueType == Long.class) {
                return this.valueType.cast((long) value);
            }
            return this.valueType.cast((double) value);
        }
        if (this == TOTAL_DOUBLE && (key.equals(UserStatKeys.KDR) || key.equals(UserStatKeys.KDA))) {
            // The ratio of raw sums: kills, deaths and assists accumulate exactly in Long before
            // the division, so totals beyond the Int range keep their exact ratio.
            long kills = TOTAL_LONG.computeValue(controller, guild, UserStatKeys.KILLS);
            long deaths = TOTAL_LONG.computeValue(controller, guild, UserStatKeys.DEATHS);
            long numerator = kills + (key.equals(UserStatKeys.KDA)
                    ? TOTAL_LONG.computeValue(controller, guild, UserStatKeys.ASSISTS) : 0);
            return this.valueType.cast(deaths == 0 ? (double) numerator : (double) numerator / deaths);
        }
        return this.valueComputer.apply(
                controller,
                guild,
                key
        );
    }

    /**
     * Selects the aggregation used for tops of {@code key}. Built-in integer user counters
     * keep their legacy behavior when averaged via {@link #AVERAGE_INT}, all other averaged
     * stats use the fractional {@link #AVERAGE}; non-averaged tops use {@link #TOTAL_LONG}
     * for integer keys (exact Long totals) and {@link #TOTAL_DOUBLE} otherwise.
     */
    public static GuildStatAggregationType<?> forTop(StatKey<?> key, boolean average) {
        boolean legacyCounter = UserStatKeys.VALUES.contains(key) && key.getValueType() == Integer.class;
        return average ? (legacyCounter ? AVERAGE_INT : AVERAGE)
                : (key.getValueType() == Integer.class ? TOTAL_LONG : TOTAL_DOUBLE);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GuildStatAggregationType)) {
            return false;
        }
        GuildStatAggregationType<?> that = (GuildStatAggregationType<?>) o;
        return Objects.equals(this.key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.key);
    }

    @Override
    public String toString() {
        return "GuildStatAggregationType{" +
               "key=" + this.key +
               ", valueType=" + this.valueType.getSimpleName() +
               '}';
    }
}

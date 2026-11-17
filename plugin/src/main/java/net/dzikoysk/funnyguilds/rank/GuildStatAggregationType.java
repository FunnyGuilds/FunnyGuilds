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

public final class GuildStatAggregationType<R extends Number> implements Keyed {

    public static final GuildStatAggregationType<Integer> TOTAL_INT = new GuildStatAggregationType<>(
            "guild_stat_aggregation_total_int",
            Integer.class,
            (controller, guild, key) -> {
                int total = 0;
                for (User member : guild.getMembers()) {
                    total += controller.getUserStatValue(
                            member,
                            key
                    ).map(Number::intValue).orElse(0);
                }
                return total;
            }
    );

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

    public static final GuildStatAggregationType<Integer> AVERAGE_INT = new GuildStatAggregationType<>(
            "guild_stat_aggregation_average_int", Integer.class,
            (service, guild, key) -> {
                Collection<User> members = guild.getMembers();
                if (members.isEmpty()) {
                    return 0;
                }
                int total = 0;
                for (User member : members) {
                    total += service.getUserStatValue(
                            member,
                            key
                    ).map(Number::intValue).orElse(0);
                }
                return total / members.size();
            }
    );

    public static final Collection<GuildStatAggregationType<?>> VALUES = ImmutableList.of(
            TOTAL_INT,
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
        if (valueType != Integer.class && valueType != Double.class) {
            throw new IllegalArgumentException("Unsupported aggregation type " + valueType);
        }
        this.key = Objects.requireNonNull(key, "aggregation key");
        this.valueType = valueType;
        this.valueComputer = Objects.requireNonNull(computer, "aggregation function");
    }

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

    public R computeValue(
            RankService controller,
            Guild guild,
            StatKey<?> key
    ) {
        Optional<Integer> nativeValue = GuildStatKeys.value(guild, key);
        if (nativeValue.isPresent()) {
            return this.valueType == Integer.class ? this.valueType.cast(nativeValue.orElseThrow())
                    : this.valueType.cast(nativeValue.orElseThrow().doubleValue());
        }
        if (this == TOTAL_DOUBLE && (key.equals(UserStatKeys.KDR) || key.equals(UserStatKeys.KDA))) {
            double kills = TOTAL_INT.computeValue(controller, guild, UserStatKeys.KILLS);
            double deaths = TOTAL_INT.computeValue(controller, guild, UserStatKeys.DEATHS);
            double numerator = kills + (key.equals(UserStatKeys.KDA)
                    ? TOTAL_INT.computeValue(controller, guild, UserStatKeys.ASSISTS) : 0);
            return this.valueType.cast(deaths == 0 ? numerator : numerator / deaths);
        }
        return this.valueComputer.apply(
                controller,
                guild,
                key
        );
    }

    public static GuildStatAggregationType<?> forTop(StatKey<?> key, boolean average) {
        boolean legacyCounter = UserStatKeys.VALUES.contains(key) && key.getValueType() == Integer.class;
        return average ? (legacyCounter ? AVERAGE_INT : AVERAGE)
                : (key.getValueType() == Integer.class ? TOTAL_INT : TOTAL_DOUBLE);
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

package net.dzikoysk.funnyguilds.rank;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
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
                    ).map(Number::intValue).orElseGet(0);
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
                    ).map(Number::doubleValue).orElseGet(0.0);
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
                    ).map(Number::doubleValue).orElseGet(0.0);
                }
                return total / memberCount;
            }
    );

    public static final Collection<GuildStatAggregationType<?>> VALUES = ImmutableList.of(
            TOTAL_INT,
            TOTAL_DOUBLE,
            AVERAGE
    );

    private final Key key;
    private final Class<R> valueType;
    private final TriFunction<RankController, Guild, StatKey<?>, R> valueComputer;

    private GuildStatAggregationType(
            @KeyPattern String key,
            Class<R> valueType,
            TriFunction<RankController, Guild, StatKey<?>, R> valueComputer
    ) {
        this.key = Key.key(
                FunnyGuilds.NAMESPACE,
                key
        );
        this.valueType = valueType;
        this.valueComputer = valueComputer;
    }

    @Override
    public @NotNull Key key() {
        return this.key;
    }

    public Class<R> getValueType() {
        return this.valueType;
    }

    public R computeValue(
            RankController controller,
            Guild guild,
            StatKey<?> key
    ) {
        return this.valueComputer.apply(
                controller,
                guild,
                key
        );
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

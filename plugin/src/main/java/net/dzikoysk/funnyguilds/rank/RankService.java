package net.dzikoysk.funnyguilds.rank;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.UnaryOperator;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildStatKeys;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserStatKeys;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;

/**
 * Maintains rankings incrementally. Positions are one-based; excluded entities keep their statistic values.
 * Mutations are serialized, including queued updates. Call refreshUser after changing ranking exemptions.
 */
public interface RankService extends AutoCloseable {

    boolean isRankedUser(User user);

    <T extends Number> RankTracker<User, T> getUserRankTracker(StatKey<T> key);

    default <T extends Number> Optional<T> getUserStatValue(User user, StatKey<T> key) {
        return this.getUserRankTracker(key).valueOfEntity(user);
    }

    default <T extends Number> Optional<Integer> getUserTopPosition(User user, StatKey<T> key) {
        return this.getUserRankTracker(key).findPositionByEntity(user);
    }

    default <T extends Number> Optional<User> getUserByTopPosition(int position, StatKey<T> key) {
        return this.getUserRankTracker(key).findEntityByPosition(position);
    }

    default Optional<RankTracker<User, ?>> getUserRankTracker(String top) {
        return StatKey.find(top).filter(key -> !GuildStatKeys.isNative(key)).map(this::getUserRankTracker);
    }

    <T extends Number> T setUserStatValue(User user, StatKey<T> key, T value);

    <T extends Number> T changeUserStatValue(User user, StatKey<T> key, UnaryOperator<T> updateFunction);

    <T extends Number> CompletableFuture<T> queueSetUserStatValue(User user, StatKey<T> key, T value);

    <T extends Number> CompletableFuture<T> queueChangeUserStatValue(User user, StatKey<T> key, UnaryOperator<T> updateFunction);

    boolean isRankedGuild(Guild guild);

    <T extends Number, R extends Number> RankTracker<Guild, R> getGuildRankTracker(
            StatKey<T> key, GuildStatAggregationType<R> aggregationType);

    default <T extends Number, R extends Number> Optional<R> getGuildStatValue(
            Guild guild, StatKey<T> key, GuildStatAggregationType<R> aggregationType) {
        return this.getGuildRankTracker(key, aggregationType).valueOfEntity(guild);
    }

    default <T extends Number, R extends Number> Optional<Integer> getGuildTopPosition(
            Guild guild, StatKey<T> key, GuildStatAggregationType<R> aggregationType) {
        return this.getGuildRankTracker(key, aggregationType).findPositionByEntity(guild);
    }

    default <T extends Number, R extends Number> Optional<Guild> getGuildByTopPosition(
            int position, StatKey<T> key, GuildStatAggregationType<R> aggregationType) {
        return this.getGuildRankTracker(key, aggregationType).findEntityByPosition(position);
    }

    default Optional<RankTracker<Guild, ?>> getGuildRankTracker(String top) {
        String normalized = top.toLowerCase(Locale.ROOT);
        boolean average = normalized.startsWith("avg_") || normalized.startsWith("avg-");
        String stat = average ? normalized.substring(4) : normalized;
        return StatKey.find(stat).filter(key -> !average || !GuildStatKeys.isNative(key)).map(key -> this.getGuildRankTracker(
                key, GuildStatAggregationType.forTop(key, average)));
    }

    /** Refresh an entity on the server thread after creation, a name change or a change to ranking exemptions. */
    void refreshUser(User user);

    /** Refresh only this guild after a membership or name change. */
    void refreshGuild(Guild guild);

    void removeUser(User user);

    void removeGuild(Guild guild);

    /** Explicit refresh for configuration reloads; no periodic full recalculation is needed. */
    void refreshAll();

    /** A consistent snapshot of written stats, including custom keys. */
    Map<StatKey<?>, Number> getUserStats(User user);

    /** Complete pending updates before saving data. Further queued updates are rejected. */
    @Override
    void close();

    @ApiStatus.Internal
    static RankService createRankService(FunnyGuilds plugin) {
        Executor executor = task -> Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        return new FunnyRankService(executor, plugin.getPluginConfiguration(), plugin.getUserManager(), plugin.getGuildManager());
    }
}

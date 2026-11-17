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

    /** Whether the user is included in user rankings: registered and, when {@code skip-privileged-players-in-rank-positions} is enabled, not holding the {@code funnyguilds.ranking.exempt} permission. Reevaluated by {@link #refreshUser(User)}. */
    boolean isRankedUser(User user);

    /**
     * Returns the tracker for the key, materializing it lazily from the currently registered users;
     * later registrations and writes are maintained incrementally. Rejects native guild keys
     * ({@link GuildStatKeys#isNative(StatKey)}) with {@link IllegalArgumentException}, since guild-owned
     * counters have no per-user values.
     */
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

    /**
     * Looks up a user tracker by top name: case-insensitive, bare names resolve in the {@code funnyguilds}
     * namespace. Native guild keys are excluded and resolve to {@link Optional#empty()}; the {@code avg_}
     * prefix has no special meaning here, so {@code avg_x} only resolves if such a user statistic exists.
     */
    default Optional<RankTracker<User, ?>> getUserRankTracker(String top) {
        return StatKey.find(top).filter(key -> !GuildStatKeys.isNative(key)).map(this::getUserRankTracker);
    }

    /**
     * Writes an absolute statistic value. Unknown keys are registered process-wide before validation.
     *
     * <p>Rejects with {@link IllegalArgumentException} when the user is not registered, the key is a
     * derived statistic ({@link UserStatKeys#KDR} or {@link UserStatKeys#KDA}), or the value is null,
     * not of the key's value type, or a non-finite {@link Double}; rejects with {@link IllegalStateException}
     * after {@link #close()}. Values are stored as given - no clamping.
     *
     * <p>Effective changes to kills, deaths and assists recompute the derived ratios. Any effective change
     * marks the user's row and, for guild members, every materialized guild aggregation of the key as
     * changed, so both persist on the next save.
     *
     * @return the written value
     */
    <T extends Number> T setUserStatValue(User user, StatKey<T> key, T value);

    /**
     * Atomically applies {@code updateFunction} to the current value, or the key's zero value when none is
     * stored, and writes the result as a single serialized read-modify-write. The function must return a
     * non-null value of the key's value type; values are not clamped. Validation and refresh effects match
     * {@link #setUserStatValue(User, StatKey, Number)}.
     */
    <T extends Number> T changeUserStatValue(User user, StatKey<T> key, UnaryOperator<T> updateFunction);

    /**
     * Schedules a write of an absolute value captured at enqueue time. If a newer value is written
     * synchronously before the queue is drained, the queued write will overwrite it with the stale
     * capture - prefer {@link #queueChangeUserStatValue(User, StatKey, UnaryOperator)} when writes
     * may interleave.
     */
    <T extends Number> CompletableFuture<T> queueSetUserStatValue(User user, StatKey<T> key, T value);

    /**
     * Schedules {@code updateFunction} to run when the update queue is drained, so it observes the value
     * current at drain time rather than at scheduling time; queued mutations stay serialized with
     * synchronous writes. The returned future completes exceptionally if the service is closed, the
     * update is rejected by {@link #changeUserStatValue(User, StatKey, UnaryOperator)} validation, or the
     * function fails. Prefer this over {@link #queueSetUserStatValue(User, StatKey, Number)}, whose
     * captured value can go stale, when writes may interleave.
     */
    <T extends Number> CompletableFuture<T> queueChangeUserStatValue(User user, StatKey<T> key, UnaryOperator<T> updateFunction);

    /** Whether the guild is included in guild rankings: registered and meeting the {@code guild-min-members} member threshold. Reevaluated by {@link #refreshGuild(Guild)}. */
    boolean isRankedGuild(Guild guild);

    /**
     * Returns the tracker for the key and aggregation pair, materializing it lazily from the currently
     * registered guilds; later registrations and writes are maintained incrementally. Trackers are
     * identified by the (key, aggregation) pair, so the same key under different aggregations keeps
     * independent rankings.
     */
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

    /**
     * Looks up a guild tracker by top name: case-insensitive, bare names resolve in the {@code funnyguilds}
     * namespace. The {@code avg_} or {@code avg-} prefix selects the average aggregation of the base
     * statistic; native guild keys are excluded from average selection since they bypass aggregation.
     *
     * <p>A custom statistic literally named {@code avg_something} whose base {@code something} also exists
     * would resolve to that average instead - keep custom statistic names unambiguous.
     */
    default Optional<RankTracker<Guild, ?>> getGuildRankTracker(String top) {
        String normalized = top.toLowerCase(Locale.ROOT);
        boolean average = normalized.startsWith("avg_") || normalized.startsWith("avg-");
        String stat = average ? normalized.substring(4) : normalized;
        return StatKey.find(stat).filter(key -> !average || !GuildStatKeys.isNative(key)).map(key -> this.getGuildRankTracker(
                key, GuildStatAggregationType.forTop(key, average)));
    }

    /** Refresh an entity on the server thread after creation, a name change or a change to ranking exemptions. */
    void refreshUser(User user);

    /** Internal: refresh only this guild after a membership or name change; coordinated {@link net.dzikoysk.funnyguilds.guild.GuildManager} operations already do it. */
    @ApiStatus.Internal
    void refreshGuild(Guild guild);

    /** Internal: removes the user from all user trackers and from the ranked set; a re-registered instance re-enters via {@link #refreshUser(User)}. Prefer {@link net.dzikoysk.funnyguilds.user.UserManager#removeUser(User)}. */
    @ApiStatus.Internal
    void removeUser(User user);

    /** Internal: removes the guild from all guild trackers; a re-registered instance re-enters via {@link #refreshGuild(Guild)}. Prefer {@link net.dzikoysk.funnyguilds.guild.GuildManager#removeGuild(Guild)}. */
    @ApiStatus.Internal
    void removeGuild(Guild guild);

    /** Internal: explicit refresh for configuration reloads; no periodic full recalculation is needed. */
    @ApiStatus.Internal
    void refreshAll();

    /** A consistent snapshot of written stats, including custom keys. */
    Map<StatKey<?>, Number> getUserStats(User user);

    /** Internal: complete pending updates before saving data. Further queued updates are rejected. */
    @Override
    @ApiStatus.Internal
    void close();

    @ApiStatus.Internal
    static RankService createRankService(FunnyGuilds plugin) {
        Executor executor = task -> Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        return new FunnyRankService(executor, plugin.getPluginConfiguration(), plugin.getUserManager(), plugin.getGuildManager());
    }
}

package net.dzikoysk.funnyguilds.rank;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.UnaryOperator;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.ApiStatus;
import panda.std.Option;

public interface RankService {

    boolean isRankedUser(User user);
    
    /**
     * @param key the statistic key
     * @param <T> the type of the statistic value
     *
     * @return the rank tracker for users based on the provided statistic key.
     */
    <T extends Number> RankTracker<User, T> getUserRankTracker(
            StatKey<T> key
    );
    
    default <T extends Number> Option<T> getUserStatValue(
            User user,
            StatKey<T> key
    ) {
        return this.getUserRankTracker(key).valueOfEntity(user);
    }
    
    default <T extends Number> Option<Integer> getUserTopPosition(
            User user,
            StatKey<T> key
    ) {
        return this.getUserRankTracker(key).findPositionByEntity(user);
    }
    
    default <T extends Number> Option<User> getUserByTopPosition(
            int position,
            StatKey<T> key
    ) {
        return this.getUserRankTracker(key).findEntityByPosition(position);
    }

    /**
     * Set the user statistic value to a specific value synchronously.
     *
     * @param user        the user whose statistic is to be set
     * @param key         the statistic key
     * @param valueChange the new value to set
     * @param <T>         the type of the statistic value
     *
     * @return the updated statistic value
     *
     * @see #changeUserStatValue(User, StatKey, UnaryOperator) for atomic updates
     * @see #queueSetUserStatValue(User, StatKey, Number) to perform the operation asynchronously (recommended)
     * 
     * @implNote this method triggers guild statistic updates if the user is a member of a guild.
     */
    <T extends Number> T setUserStatValue(
            User user,
            StatKey<T> key,
            T valueChange
    );

    /**
     * Atomically change the user statistic value using an update function synchronously.
     *
     * @param user           the user whose statistic is to be changed
     * @param key            the statistic key
     * @param updateFunction the function to update the statistic value
     * @param <T>            the type of the statistic value
     *
     * @return the updated statistic value
     *
     * @apiNote this method atomically updates the statistic value based on its current state,
     * making it suitable for scenarios like incrementing or decrementing values.
     * @see #queueChangeUserStatValue(User, StatKey, UnaryOperator) to perform the operation asynchronously 
     * (recommended)
     */
    <T extends Number> T changeUserStatValue(
            User user,
            StatKey<T> key,
            UnaryOperator<T> updateFunction
    );

    /**
     * Asynchronously queue the setting of a user statistic value.
     *
     * @param user        the user whose statistic is to be set
     * @param key         the statistic key
     * @param valueChange the new value to set
     * @param <T>         the type of the statistic value
     *
     * @return a CompletableFuture that will complete with the updated statistic value
     *
     * @see #changeUserStatValue(User, StatKey, UnaryOperator) for atomic updates
     * @see #queueChangeUserStatValue(User, StatKey, UnaryOperator) for atomic updates asynchronously (recommended)
     */
    <T extends Number> CompletableFuture<T> queueSetUserStatValue(
            User user,
            StatKey<T> key,
            T valueChange
    );

    /**
     * Asynchronously queue the atomic change of a user statistic value using an update function.
     *
     * @param user           the user whose statistic is to be changed
     * @param key            the statistic key
     * @param updateFunction the function to update the statistic value
     * @param <T>            the type of the statistic value
     *
     * @return a CompletableFuture that will complete with the updated statistic value
     *
     * @apiNote this method atomically updates the statistic value based on its current state,
     * making it suitable for scenarios like incrementing or decrementing values.
     */
    <T extends Number> CompletableFuture<T> queueChangeUserStatValue(
            User user,
            StatKey<T> key,
            UnaryOperator<T> updateFunction
    );
    
    boolean isRankedGuild(Guild guild);

    /**
     * Get the rank tracker for guilds based on a specific statistic key and aggregation type.
     *
     * @param key             the statistic key
     * @param aggregationType the guild statistic aggregation type
     * @param <T>             the type of the statistic value
     * @param <R>             the type of the aggregated statistic value
     *
     * @return the rank tracker for guilds
     */
    <T extends Number, R extends Number> RankTracker<Guild, R> getGuildRankTracker(
            StatKey<T> key,
            GuildStatAggregationType<R> aggregationType
    );
    
    default <T extends Number, R extends Number> Option<R> getGuildStatValue(
            Guild guild,
            StatKey<T> key,
            GuildStatAggregationType<R> aggregationType
    ) {
        return this.getGuildRankTracker(key, aggregationType).valueOfEntity(guild);
    }
    
    default <T extends Number, R extends Number> Option<Integer> getGuildTopPosition(
            Guild guild,
            StatKey<T> key,
            GuildStatAggregationType<R> aggregationType
    ) {
        return this.getGuildRankTracker(key, aggregationType).findPositionByEntity(guild);
    }
    
    default <T extends Number, R extends Number> Option<Guild> getGuildByTopPosition(
            int position,
            StatKey<T> key,
            GuildStatAggregationType<R> aggregationType
    ) {
        return this.getGuildRankTracker(key, aggregationType).findEntityByPosition(position);
    }

    @ApiStatus.Internal
    static RankService createRankService(FunnyGuilds plugin) {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        Executor executor = task -> scheduler.runTaskAsynchronously(
                plugin,
                task
        );
        return new FunnyRankService(
                executor,
                plugin.getPluginConfiguration(),
                plugin.getUserManager(),
                plugin.getGuildManager()
        );
    }

}

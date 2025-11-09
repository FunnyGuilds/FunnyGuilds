package net.dzikoysk.funnyguilds.rank;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.function.UnaryOperator;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;

final class FunnyRankService implements RankService {
    
    private final Executor executor;
    private final Queue<Runnable> rankUpdateQueue = new ConcurrentLinkedQueue<>();
    
    private final PluginConfiguration pluginConfiguration;
    
    private final UserManager userManager;
    private final Cache<StatKey<?>, UserRankTracker<?>> userRankTrackers = Caffeine.newBuilder().build();
    
    private final GuildManager guildManager;
    private final Cache<StatKey<?>, GuildRankTracker<?>> guildRankTrackers = Caffeine.newBuilder().build();
    
    @SuppressWarnings("unchecked")
    FunnyRankService(
            Executor executor,
            PluginConfiguration pluginConfiguration,
            UserManager userManager,
            GuildManager guildManager
    ) {
        this.executor = executor;
        this.pluginConfiguration = pluginConfiguration;
        
        this.userManager = userManager;
        this.guildManager = guildManager;
    }

    @Override
    public <T extends Number> T setUserStatValue(
            User user,
            StatKey<T> key,
            T valueChange
    ) {
        T updatedValue = this.getUserRankTracker(key).setValue(user, valueChange);
        user.getGuild().peek(guild -> this.updateGuildStatsValue(guild, key));
        return updatedValue;
    }

    @Override
    public <T extends Number> T changeUserStatValue(
            User user,
            StatKey<T> key,
            UnaryOperator<T> updateFunction
    ) {
        T updatedValue = this.getUserRankTracker(key).changeValue(user, updateFunction);
        user.getGuild().peek(guild -> this.updateGuildStatsValue(guild, key));
        return updatedValue;
    }

    @Override
    public <T extends Number> CompletableFuture<T> queueSetUserStatValue(
            User user,
            StatKey<T> key,
            T valueChange
    ) {
        CompletableFuture<T> future = new CompletableFuture<>();
        this.rankUpdateQueue.add(() -> future.complete(this.setUserStatValue(user, key, valueChange)));
        this.executor.execute(this::processRankUpdates);
        return future;
    }

    @Override
    public <T extends Number> CompletableFuture<T> queueChangeUserStatValue(
            User user,
            StatKey<T> key,
            UnaryOperator<T> updateFunction
    ) {
        CompletableFuture<T> future = new CompletableFuture<>();
        this.rankUpdateQueue.add(() -> future.complete(this.changeUserStatValue(user, key, updateFunction)));
        this.executor.execute(this::processRankUpdates);
        return future;
    }
    
    private void processRankUpdates() {
        Runnable task;
        while ((task = this.rankUpdateQueue.poll()) != null) {
            task.run();
        }
    }

    private <T extends Number> void updateGuildStatsValue(
            Guild guild,
            StatKey<T> key
    ) {
        GuildStatAggregationType.VALUES
                .forEach(statType -> this.updateGuildStatValue(guild, key, statType));
    }
    
    private <T extends Number, R extends Number> void updateGuildStatValue(
            Guild guild,
            StatKey<T> key,
            GuildStatAggregationType<R> aggregationType
    ) {
        R newValue = aggregationType.computeValue(this, guild, key);
        this.getGuildRankTracker(key, aggregationType).setValue(guild, newValue);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Number> UserRankTracker<T> getUserRankTracker(StatKey<T> key) {
        return (UserRankTracker<T>) this.userRankTrackers.get(key, k -> new UserRankTracker<>(
                key,
                this.userManager,
                this::isRankedUser
        ));
    }
    
    private boolean isRankedUser(User user) {
        return !this.pluginConfiguration.skipPrivilegedPlayersInRankPositions || !user.hasPermission("funnyguilds.ranking.exempt");
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public  <T extends Number, R extends Number> GuildRankTracker<R> getGuildRankTracker(
            StatKey<T> key,
            GuildStatAggregationType<R> statType
    ) {
        StatKey<R> finalKey = createStatKey(statType, key);
        return (GuildRankTracker<R>) this.guildRankTrackers.get(finalKey, k -> new GuildRankTracker<>(
                finalKey,
                this.guildManager,
                this::isRankedGuild
        ));
    }
    
    private boolean isRankedGuild(Guild guild) {
        return guild.getMembers().size() >= this.pluginConfiguration.minMembersToInclude;
    }
    
    @SuppressWarnings("unchecked")
    private static <R extends Number> StatKey<R> createStatKey(GuildStatAggregationType<R> statType, StatKey<?> key) {
        String newKey = key.key().value() + "-" + statType.key().value();
        if (statType.getValueType().isAssignableFrom(Double.class)) {
            return (StatKey<R>) StatKey.doubleStatKey(newKey);
        } else if (statType.getValueType().isAssignableFrom(Integer.class)) {
            return (StatKey<R>) StatKey.integerStatKey(newKey);
        } else {
            throw new IllegalArgumentException("Unsupported stat type: " + statType.getValueType());
        }
    }
}

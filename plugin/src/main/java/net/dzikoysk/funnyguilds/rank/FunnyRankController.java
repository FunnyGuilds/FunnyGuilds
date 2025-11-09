package net.dzikoysk.funnyguilds.rank;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.function.UnaryOperator;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.GuildRankNameProvider;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import net.dzikoysk.funnyguilds.user.UserRankNameProvider;
import panda.std.Option;

public final class FunnyRankController implements RankController {
    
    private final Executor executor;
    private final Queue<Runnable> rankUpdateQueue = new ConcurrentLinkedQueue<>();
    
    private final PluginConfiguration pluginConfiguration;
    
    private final UserManager userManager;
    private final UserRankNameProvider userRankNameProvider;
    private final LoadingCache<StatKey<?>, UserRankTracker<?>> userRankTrackers;
    
    private final GuildManager guildManager;
    private final GuildRankNameProvider guildRankNameProvider;
    private final Cache<StatKey<?>, GuildRankTracker<?>> guildRankTrackers;
    
    @SuppressWarnings("unchecked")
    public FunnyRankController(
            Executor executor,
            PluginConfiguration pluginConfiguration,
            UserManager userManager,
            GuildManager guildManager
    ) {
        this.executor = executor;
        this.pluginConfiguration = pluginConfiguration;
        
        this.userManager = userManager;
        this.userRankNameProvider = new UserRankNameProvider(userManager);
        this.userRankTrackers = Caffeine.newBuilder()
                .build(key -> new UserRankTracker<>(
                        (StatKey<Number>) key,
                        this.userRankNameProvider,
                        this.pluginConfiguration.skipPrivilegedPlayersInRankPositions
                ));

        this.guildManager = guildManager;
        this.guildRankNameProvider = new GuildRankNameProvider(guildManager);
        this.guildRankTrackers = Caffeine.newBuilder().build();
    }
    
    @Override
    public <T extends Number> Option<T> getUserStatValue(User user, StatKey<T> key) {
        return this.getUserRankTracker(key).getValue(user);
    }
    
    @Override
    public <T extends Number> Option<Integer> getUserPosition(
            User user,
            StatKey<T> key
    ) {
        return this.getUserRankTracker(key).getPosition(user);
    }

    @Override
    public <T extends Number> Option<User> getUserAtPosition(
            int position,
            StatKey<T> key
    ) {
        return this.getUserRankTracker(key)
                .getAtPosition(position)
                .flatMap(this.userManager::findByUuid);
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

    @Override
    public <T extends Number, R extends Number> Option<R> getGuildStatValue(
            Guild guild,
            StatKey<T> key,
            GuildStatAggregationType<R> aggregationType
    ) {
        return this.getGuildRankTracker(key, aggregationType).getValue(guild);
    }
    
    @Override
    public <T extends Number> Option<Integer> getGuildPosition(
            Guild guild,
            StatKey<T> key,
            GuildStatAggregationType<?> aggregationType
    ) {
        return this.getGuildRankTracker(key, aggregationType).getPosition(guild);
    }

    @Override
    public <T extends Number> Option<Guild> getGuildAtPosition(
            int position,
            StatKey<T> key,
            GuildStatAggregationType<?> aggregationType
    ) {
        return this.getGuildRankTracker(key, aggregationType)
                .getAtPosition(position)
                .flatMap(this.guildManager::findByUuid);
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

    @SuppressWarnings("unchecked")
    private <T extends Number> UserRankTracker<T> getUserRankTracker(StatKey<T> key) {
        return (UserRankTracker<T>) this.userRankTrackers.get(key);
    }
    
    
    @SuppressWarnings("unchecked")
    private <T extends Number, R extends Number> GuildRankTracker<R> getGuildRankTracker(
            StatKey<T> key,
            GuildStatAggregationType<R> statType
    ) {
        StatKey<R> finalKey = createStatKey(statType, key);
        return (GuildRankTracker<R>) this.guildRankTrackers.get(finalKey, k -> new GuildRankTracker<>(
                (StatKey<Number>) k,
                this.guildRankNameProvider,
                this::isRankedGuild
        ));
    }
    
    public boolean isRankedGuild(Guild guild) {
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

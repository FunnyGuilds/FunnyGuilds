package net.dzikoysk.funnyguilds.rank;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildRankNameProvider;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserRankNameProvider;
import panda.std.Option;

public final class FunnyRankController implements RankController {
    
    private final Executor executor;
    private final PluginConfiguration pluginConfiguration;
    
    private final UserRankNameProvider userRankNameProvider;
    private final LoadingCache<StatKey<?>, UserRankTracker<?>> userRankTrackers;
    
    private final GuildRankNameProvider guildRankNameProvider;
    private final Cache<StatKey<?>, GuildRankTracker<?>> guildRankTrackers;
    
    private FunnyRankController(
            Executor executor,
            PluginConfiguration pluginConfiguration,
            UserRankNameProvider userRankNameProvider,
            GuildRankNameProvider guildRankNameProvider
    ) {
        this.executor = executor;
        this.pluginConfiguration = pluginConfiguration;
        
        this.userRankNameProvider = userRankNameProvider;
        this.userRankTrackers = this.createUseRankTrackersCache();
        
        this.guildRankNameProvider = guildRankNameProvider;
        this.guildRankTrackers = this.createGuildRankTrackersCache();
    }
    
    @Override
    public <T extends Number> Option<T> getUserStatValue(User user, StatKey<T> key) {
        return this.getUserRankTracker(key).getValue(user);
    }

    @Override
    public <T extends Number> CompletableFuture<T> updateUserStatValue(
            User user,
            StatKey<T> key,
            T valueChange
    ) {
        return this.getUserRankTracker(key)
                .updateValue(user, valueChange)
                .thenCompose(v -> user.getGuild()
                        .map(guild -> this.updateGuildStatsValue(guild, key))
                        .orElseGet(CompletableFuture.completedFuture(null))
                        .thenApply(ignored -> v));
    }

    @Override
    public <T extends Number, R extends Number> Option<R> getGuildStatValue(
            Guild guild,
            StatKey<T> key,
            GuildStatAggregationType<R> aggregationType
    ) {
        return this.getGuildRankTracker(key, aggregationType).getValue(guild);
    }
    
    private <T extends Number> CompletableFuture<?> updateGuildStatsValue(
            Guild guild,
            StatKey<T> key
    ) {
        return CompletableFuture.completedFuture(GuildStatAggregationType.VALUES.stream()
                .map(statType -> this.updateGuildStatValue(guild, key, statType))
                .toArray(CompletableFuture[]::new));
    }
    
    private <T extends Number, R extends Number> CompletableFuture<?> updateGuildStatValue(
            Guild guild,
            StatKey<T> key,
            GuildStatAggregationType<R> aggregationType
    ) {
        R newValue = aggregationType.computeValue(this, guild, key);
        return this.getGuildRankTracker(key, aggregationType).setValue(guild, newValue);
    }

    @SuppressWarnings("unchecked")
    private <T extends Number> UserRankTracker<T> getUserRankTracker(StatKey<T> key) {
        return (UserRankTracker<T>) this.userRankTrackers.get(key);
    }
    
    @SuppressWarnings("unchecked")
    private LoadingCache<StatKey<?>, UserRankTracker<?>> createUseRankTrackersCache() {
        return Caffeine.newBuilder()
                .build(key -> new UserRankTracker<>(
                        this.executor,
                        (StatKey<Number>) key,
                        this.userRankNameProvider,
                        this.pluginConfiguration.skipPrivilegedPlayersInRankPositions
                ));
    }
    
    @SuppressWarnings("unchecked")
    private <T extends Number, R extends Number> GuildRankTracker<R> getGuildRankTracker(
            StatKey<T> key,
            GuildStatAggregationType<R> statType
    ) {
        StatKey<R> finalKey = createStatKey(statType, key);
        return (GuildRankTracker<R>) this.guildRankTrackers.get(finalKey, k -> new GuildRankTracker<>(
                this.executor,
                (StatKey<Number>) k,
                this.guildRankNameProvider,
                this::isRankedGuild
        ));
    }
    
    private Cache<StatKey<?>, GuildRankTracker<?>> createGuildRankTrackersCache() {
        return Caffeine.newBuilder()
                .build();
    }
    
    public boolean isRankedGuild(Guild guild) {
        return guild.getMembers().size() >= this.pluginConfiguration.minMembersToInclude;
    }
    
    @SuppressWarnings("unchecked")
    private static <R extends Number> StatKey<R> createStatKey(GuildStatAggregationType<R> statType, StatKey<?> key) {
        String newKey = key.key().asString() + "_" + statType.key().asString();
        if (statType.getValueType().isAssignableFrom(Double.class)) {
            return (StatKey<R>) StatKey.doubleStatKey(newKey);
        } else if (statType.getValueType().isAssignableFrom(Integer.class)) {
            return (StatKey<R>) StatKey.integerStatKey(newKey);
        } else {
            throw new IllegalArgumentException("Unsupported stat type: " + statType.getValueType());
        }
    }
}

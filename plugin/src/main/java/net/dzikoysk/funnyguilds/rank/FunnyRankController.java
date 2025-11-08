package net.dzikoysk.funnyguilds.rank;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserRankNameProvider;
import panda.std.Option;

public final class FunnyRankController implements RankController {
    
    private final Executor executor;
    private final PluginConfiguration pluginConfiguration;
    
    private final UserRankNameProvider userRankNameProvider;
    private final LoadingCache<StatKey<?>, UserRankTracker<?>> userRankTrackers;
    
    private FunnyRankController(
            Executor executor,
            PluginConfiguration pluginConfiguration,
            UserRankNameProvider userRankNameProvider
    ) {
        this.executor = executor;
        this.pluginConfiguration = pluginConfiguration;
        this.userRankNameProvider = userRankNameProvider;
        
        this.userRankTrackers = this.createUseRankTrackersCache();
    }
    
    @Override
    public <T extends Number> Option<T> getUserStatValue(User user, StatKey<T> key) {
        return this.getUserRankTracker(key).getValue(user);
    }

    @Override
    public <T extends Number> CompletableFuture<Void> updateUserStatValue(
            User user,
            StatKey<T> key,
            T valueChange
    ) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        this.getUserRankTracker(key).updateValue(user, valueChange, result -> future.complete(null));
        return future;
    }

    @Override
    public <R extends Number> Option<R> getGuildStatValue(
            Guild guild,
            StatKey<?> key,
            GuildStatType<R> statType
    ) {
        throw new UnsupportedOperationException("Guild stats are not supported yet");
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
}

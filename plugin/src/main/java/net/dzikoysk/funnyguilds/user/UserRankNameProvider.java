package net.dzikoysk.funnyguilds.user;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.time.Duration;
import java.util.UUID;
import net.dzikoysk.funnyguilds.rank.RankNameProvider;

public final class UserRankNameProvider implements RankNameProvider {

    private final LoadingCache<UUID, String> cache;

    public UserRankNameProvider(UserManager userManager) {
        this.cache = Caffeine.newBuilder()
                .maximumSize(1000)
                .initialCapacity(100)
                .expireAfterWrite(Duration.ofMinutes(10))
                .expireAfterAccess(Duration.ofMinutes(5))
                .build(uuid -> userManager.findByUuid(uuid)
                        .map(User::getName)
                        .orElseGet("Unknown"));

    }

    @Override
    public String apply(UUID uuid) {
        return this.cache.get(uuid);
    }

}

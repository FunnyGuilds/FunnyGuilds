package net.dzikoysk.funnyguilds.guild;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.time.Duration;
import java.util.UUID;
import net.dzikoysk.funnyguilds.rank.RankNameProvider;

public final class GuildRankNameProvider implements RankNameProvider {
    
    private final LoadingCache<UUID, String> cache;

    public GuildRankNameProvider(GuildManager guildManager) {
        this.cache = Caffeine.newBuilder()
                .maximumSize(1000)
                .initialCapacity(100)
                .expireAfterWrite(Duration.ofMinutes(10))
                .expireAfterAccess(Duration.ofMinutes(5))
                .build(uuid -> guildManager.findByUuid(uuid)
                        .map(Guild::getName)
                        .orElseGet("Unknown"));
    }

    @Override
    public String apply(UUID uuid) {
        return this.cache.get(uuid);
    }
}

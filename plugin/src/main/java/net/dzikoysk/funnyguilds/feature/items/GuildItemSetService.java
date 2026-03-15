package net.dzikoysk.funnyguilds.feature.items;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemSet;
import net.dzikoysk.funnyguilds.config.sections.items.ItemsConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GuildItemSetService {

    private final ItemsConfiguration config;

    private final Cache<UUID, GuildItemSet> cache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(30))
            .executor(Runnable::run)
            .build();

    public GuildItemSetService(ItemsConfiguration config) {
        this.config = config;
    }

    public GuildItemSet getSetForPlayer(Player player) {
        return cache.get(player.getUniqueId(), uuid -> computeSetForPlayer(player));
    }

    public GuildItemSet getSetForPlayer(UUID uuid) {
        return cache.get(uuid, id -> {
            Player online = Bukkit.getPlayer(id);
            if (online != null) {
                return computeSetForPlayer(online);
            }
            return getDefaultSet();
        });
    }

    public void invalidate(UUID uuid) {
        cache.invalidate(uuid);
    }

    public void invalidateAll() {
        cache.invalidateAll();
    }


    private GuildItemSet computeSetForPlayer(Player player) {
        List<GuildItemSet> sorted = config.getSetsSortedByPriority();
        return sorted.stream()
                .filter(set -> set.permission == null || player.hasPermission(set.permission))
                .findFirst()
                .orElseGet(this::getDefaultSet);
    }

    private GuildItemSet getDefaultSet() {
        List<GuildItemSet> sorted = config.getSetsSortedByPriority();
        if (sorted.isEmpty()) {
            return new GuildItemSet();
        }

        return sorted.getLast();
    }

}


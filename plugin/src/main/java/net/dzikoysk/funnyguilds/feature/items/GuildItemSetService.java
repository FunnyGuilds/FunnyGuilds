package net.dzikoysk.funnyguilds.feature.items;

import java.util.List;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemSet;
import net.dzikoysk.funnyguilds.config.sections.items.ItemsConfiguration;
import org.bukkit.entity.Player;

public class GuildItemSetService {

    private final ItemsConfiguration config;

    public GuildItemSetService(ItemsConfiguration config) {
        this.config = config;
    }

    public GuildItemSet getSetForPlayer(Player player) {
        List<GuildItemSet> sorted = config.getSetsSortedByPriority();
        return sorted.stream()
                .filter(set -> set.permission == null || player.hasPermission(set.permission))
                .findFirst()
                .orElseGet(() -> sorted.isEmpty() ? new GuildItemSet() : sorted.get(sorted.size() - 1));
    }

}

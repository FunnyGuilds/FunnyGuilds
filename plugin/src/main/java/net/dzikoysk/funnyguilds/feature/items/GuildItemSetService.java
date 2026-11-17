package net.dzikoysk.funnyguilds.feature.items;

import java.util.List;
import java.util.Map;
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
                .orElseGet(() -> sorted.stream()
                        .filter(set -> set.permission == null)
                        .findFirst()
                        .orElseGet(GuildItemSet::new));
    }

    public String getNameForSet(GuildItemSet set) {
        return config.getGuildItemSets().entrySet().stream()
                .filter(entry -> entry.getValue() == set)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("default");
    }

}

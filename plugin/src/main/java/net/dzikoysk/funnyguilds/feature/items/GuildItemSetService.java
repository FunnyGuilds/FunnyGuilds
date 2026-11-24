package net.dzikoysk.funnyguilds.feature.items;

import java.util.List;
import java.util.Map;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemSet;
import net.dzikoysk.funnyguilds.config.sections.items.ItemsConfiguration;
import net.dzikoysk.funnyguilds.config.sections.items.ItemsSnapshot;
import org.bukkit.entity.Player;

public class GuildItemSetService {

    private final ItemsConfiguration config;

    public GuildItemSetService(ItemsConfiguration config) {
        this.config = config;
    }

    public ItemsSnapshot.ItemSet getSetForPlayer(Player player) {
        return getSetForPlayer(player, this.config.snapshot());
    }

    public ItemsSnapshot.ItemSet getSetForPlayer(Player player, ItemsSnapshot snapshot) {
        List<ItemsSnapshot.ItemSet> sorted = snapshot.getSetsSortedByPriority();
        return sorted.stream()
                .filter(set -> set.permission == null || player.hasPermission(set.permission))
                .findFirst()
                .orElseGet(() -> sorted.stream()
                        .filter(set -> set.permission == null)
                        .findFirst()
                        .orElseGet(() -> new ItemsSnapshot.ItemSet(new GuildItemSet())));
    }

    public String getNameForSet(ItemsSnapshot.ItemSet set) {
        return getNameForSet(set, this.config.snapshot());
    }

    public String getNameForSet(ItemsSnapshot.ItemSet set, ItemsSnapshot snapshot) {
        return snapshot.getGuildItemSets().entrySet().stream()
                .filter(entry -> entry.getValue() == set)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("default");
    }

}

package net.dzikoysk.funnyguilds.config.serdes;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import java.util.LinkedHashMap;
import java.util.Map;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemSet;
import net.dzikoysk.funnyguilds.config.sections.items.SetRequirements;
import org.jetbrains.annotations.NotNull;

public class GuildItemSetSerializer implements ObjectSerializer<GuildItemSet> {

    @Override
    public boolean supports(@NotNull Class<?> type) {
        return GuildItemSet.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NotNull GuildItemSet set, @NotNull SerializationData data, @NotNull GenericsDeclaration generics) {
        data.add("permission", set.permission != null ? set.permission : "");
        data.add("priority", set.priority);
        data.add("requirements", set.requirements != null ? set.requirements : new SetRequirements(), SetRequirements.class);
        data.add("items", set.items != null ? set.items : new LinkedHashMap<>());
        data.add("required-experience", set.requiredExperience);
        data.add("required-money", set.requiredMoney);
        data.add("required-rank", set.requiredRank);
    }

    @SuppressWarnings("unchecked")
    @Override
    public GuildItemSet deserialize(@NotNull DeserializationData data, @NotNull GenericsDeclaration generics) {
        GuildItemSet set = new GuildItemSet();
        String permission = data.containsKey("permission") ? data.get("permission", String.class) : "";
        set.permission = (permission != null && !permission.isEmpty()) ? permission : null;
        set.priority = data.containsKey("priority") ? data.get("priority", int.class) : 0;
        set.requirements = data.containsKey("requirements") ? data.get("requirements", SetRequirements.class) : new SetRequirements();

        if (data.containsKey("items")) {
            Map<String, Object> rawItems = data.get("items", Map.class);
            LinkedHashMap<String, Integer> items = new LinkedHashMap<>();
            if (rawItems != null) {
                for (Map.Entry<String, Object> entry : rawItems.entrySet()) {
                    items.put(entry.getKey(), ((Number) entry.getValue()).intValue());
                }
            }
            set.items = items;
        } else {
            set.items = new LinkedHashMap<>();
        }

        set.requiredExperience = data.containsKey("required-experience") ? data.get("required-experience", int.class) : 0;
        set.requiredMoney = data.containsKey("required-money") ? data.get("required-money", double.class) : 0.0;
        set.requiredRank = data.containsKey("required-rank") ? data.get("required-rank", int.class) : 0;

        return set;
    }

}


package net.dzikoysk.funnyguilds.config.serdes;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import java.util.ArrayList;
import java.util.List;
import net.dzikoysk.funnyguilds.config.sections.items.ItemDisplayConfig;
import org.jetbrains.annotations.NotNull;

public class ItemDisplayConfigSerializer implements ObjectSerializer<ItemDisplayConfig> {

    @Override
    public boolean supports(@NotNull Class<?> type) {
        return ItemDisplayConfig.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NotNull ItemDisplayConfig config, @NotNull SerializationData data, @NotNull GenericsDeclaration generics) {
        data.add("glow", config.glow);
        data.add("name-prefix", config.namePrefix != null ? config.namePrefix : "");
        data.add("additional-lore", config.additionalLore != null ? config.additionalLore : new ArrayList<>());
    }

    @Override
    public ItemDisplayConfig deserialize(@NotNull DeserializationData data, @NotNull GenericsDeclaration generics) {
        ItemDisplayConfig config = new ItemDisplayConfig();
        config.glow = data.containsKey("glow") ? data.get("glow", boolean.class) : false;
        config.namePrefix = data.containsKey("name-prefix") ? data.get("name-prefix", String.class) : null;
        config.additionalLore = data.containsKey("additional-lore")
                ? new ArrayList<>(data.getAsList("additional-lore", String.class))
                : new ArrayList<>();
        return config;
    }

}


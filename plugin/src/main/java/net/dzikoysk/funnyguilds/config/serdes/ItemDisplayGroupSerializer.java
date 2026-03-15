package net.dzikoysk.funnyguilds.config.serdes;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import net.dzikoysk.funnyguilds.config.sections.items.GuiConfiguration.ItemDisplayGroup;
import net.dzikoysk.funnyguilds.config.sections.items.ItemDisplayConfig;
import org.jetbrains.annotations.NotNull;

public class ItemDisplayGroupSerializer implements ObjectSerializer<ItemDisplayGroup> {

    @Override
    public boolean supports(@NotNull Class<?> type) {
        return ItemDisplayGroup.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NotNull ItemDisplayGroup group, @NotNull SerializationData data, @NotNull GenericsDeclaration generics) {
        data.add("has-enough", group.hasEnough, ItemDisplayConfig.class);
        data.add("missing", group.missing, ItemDisplayConfig.class);
    }

    @Override
    public ItemDisplayGroup deserialize(@NotNull DeserializationData data, @NotNull GenericsDeclaration generics) {
        ItemDisplayGroup group = new ItemDisplayGroup();
        if (data.containsKey("has-enough")) {
            group.hasEnough = data.get("has-enough", ItemDisplayConfig.class);
        }
        if (data.containsKey("missing")) {
            group.missing = data.get("missing", ItemDisplayConfig.class);
        }
        return group;
    }

}


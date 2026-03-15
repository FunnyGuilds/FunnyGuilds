package net.dzikoysk.funnyguilds.config.serdes;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import java.util.ArrayList;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemDefinition;
import org.jetbrains.annotations.NotNull;

public class GuildItemDefinitionSerializer implements ObjectSerializer<GuildItemDefinition> {

    @Override
    public boolean supports(@NotNull Class<?> type) {
        return GuildItemDefinition.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NotNull GuildItemDefinition def, @NotNull SerializationData data, @NotNull GenericsDeclaration generics) {
        data.add("material", def.material);
        data.add("name", def.name != null ? def.name : "");
        data.add("lore", def.lore != null ? def.lore : new ArrayList<>());
        data.add("enchants", def.enchants != null ? def.enchants : new ArrayList<>());
        data.add("flags", def.flags != null ? def.flags : new ArrayList<>());
        if (def.customModelData != null) {
            data.add("custom-model-data", def.customModelData);
        }
        data.add("skull-owner", def.skullOwner != null ? def.skullOwner : "");
        data.add("armor-color", def.armorColor != null ? def.armorColor : "");
    }

    @Override
    public GuildItemDefinition deserialize(@NotNull DeserializationData data, @NotNull GenericsDeclaration generics) {
        GuildItemDefinition def = new GuildItemDefinition();

        def.material = data.containsKey("material") ? data.get("material", String.class) : "STONE";
        def.name = data.containsKey("name") ? data.get("name", String.class) : "";
        def.lore = data.containsKey("lore") ? new ArrayList<>(data.getAsList("lore", String.class)) : new ArrayList<>();
        def.enchants = data.containsKey("enchants") ? new ArrayList<>(data.getAsList("enchants", String.class)) : new ArrayList<>();
        def.flags = data.containsKey("flags") ? new ArrayList<>(data.getAsList("flags", String.class)) : new ArrayList<>();
        def.customModelData = data.containsKey("custom-model-data") ? data.get("custom-model-data", Integer.class) : null;
        def.skullOwner = data.containsKey("skull-owner") ? data.get("skull-owner", String.class) : "";
        def.armorColor = data.containsKey("armor-color") ? data.get("armor-color", String.class) : "";

        return def;
    }

}


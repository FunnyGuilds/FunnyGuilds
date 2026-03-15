package net.dzikoysk.funnyguilds.config.serdes;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import java.util.LinkedHashMap;
import java.util.Map;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemDefinition;
import net.dzikoysk.funnyguilds.config.sections.items.PerSetConfig;
import org.jetbrains.annotations.NotNull;

public class PerSetConfigSerializer implements ObjectSerializer<PerSetConfig> {

    @Override
    public boolean supports(@NotNull Class<?> type) {
        return PerSetConfig.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NotNull PerSetConfig config, @NotNull SerializationData data, @NotNull GenericsDeclaration generics) {
        data.add("title", config.title != null ? config.title : "");
        Map<String, GuildItemDefinition> overrides = config.libraryOverrides != null ? config.libraryOverrides : new LinkedHashMap<>();
        data.add("library-overrides", overrides);
    }

    @SuppressWarnings("unchecked")
    @Override
    public PerSetConfig deserialize(@NotNull DeserializationData data, @NotNull GenericsDeclaration generics) {
        PerSetConfig config = new PerSetConfig();
        String title = data.containsKey("title") ? data.get("title", String.class) : "";
        config.title = (title != null && !title.isEmpty()) ? title : null;

        LinkedHashMap<String, GuildItemDefinition> overrides = new LinkedHashMap<>();
        if (data.containsKey("library-overrides")) {
            Map<String, Object> rawOverrides = data.get("library-overrides", Map.class);
            if (rawOverrides != null) {
                for (Map.Entry<String, Object> entry : rawOverrides.entrySet()) {
                    Object value = entry.getValue();
                    if (value instanceof GuildItemDefinition def) {
                        overrides.put(entry.getKey(), def);
                    } else if (value instanceof Map) {
                        // Surowa mapa z YAML — ręczna deserializacja
                        Map<String, Object> rawDef = (Map<String, Object>) value;
                        GuildItemDefinition def = deserializeItemDef(rawDef);
                        overrides.put(entry.getKey(), def);
                    }
                }
            }
        }
        config.libraryOverrides = overrides;

        return config;
    }

    private static GuildItemDefinition deserializeItemDef(Map<String, Object> raw) {
        GuildItemDefinition def = new GuildItemDefinition();
        def.material = raw.containsKey("material") ? String.valueOf(raw.get("material")) : "STONE";
        def.name = raw.containsKey("name") ? String.valueOf(raw.get("name")) : "";
        def.lore = raw.containsKey("lore") ? toStringList(raw.get("lore")) : new java.util.ArrayList<>();
        def.enchants = raw.containsKey("enchants") ? toStringList(raw.get("enchants")) : new java.util.ArrayList<>();
        def.flags = raw.containsKey("flags") ? toStringList(raw.get("flags")) : new java.util.ArrayList<>();
        def.customModelData = raw.containsKey("custom-model-data") ? ((Number) raw.get("custom-model-data")).intValue() : null;
        def.skullOwner = raw.containsKey("skull-owner") ? String.valueOf(raw.get("skull-owner")) : "";
        def.armorColor = raw.containsKey("armor-color") ? String.valueOf(raw.get("armor-color")) : "";
        return def;
    }

    private static java.util.List<String> toStringList(Object obj) {
        if (obj instanceof java.util.List<?> list) {
            java.util.List<String> result = new java.util.ArrayList<>();
            for (Object item : list) {
                result.add(String.valueOf(item));
            }
            return result;
        }
        return new java.util.ArrayList<>();
    }

}



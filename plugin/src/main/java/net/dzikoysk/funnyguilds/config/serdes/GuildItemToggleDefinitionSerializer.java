package net.dzikoysk.funnyguilds.config.serdes;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemDefinition;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemToggleDefinition;
import org.jetbrains.annotations.NotNull;

public class GuildItemToggleDefinitionSerializer implements ObjectSerializer<GuildItemToggleDefinition> {

    @Override
    public boolean supports(@NotNull Class<?> type) {
        return GuildItemToggleDefinition.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NotNull GuildItemToggleDefinition toggle, @NotNull SerializationData data, @NotNull GenericsDeclaration generics) {
        data.add("all", toggle.all, GuildItemDefinition.class);
        data.add("missing-only", toggle.missingOnly, GuildItemDefinition.class);
    }

    @Override
    public GuildItemToggleDefinition deserialize(@NotNull DeserializationData data, @NotNull GenericsDeclaration generics) {
        GuildItemToggleDefinition toggle = new GuildItemToggleDefinition();
        toggle.all = data.get("all", GuildItemDefinition.class);
        toggle.missingOnly = data.get("missing-only", GuildItemDefinition.class);
        return toggle;
    }

}


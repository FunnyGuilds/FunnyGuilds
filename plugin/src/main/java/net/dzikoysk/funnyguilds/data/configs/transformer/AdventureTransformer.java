package net.dzikoysk.funnyguilds.data.configs.transformer;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.SerdesContext;
import eu.okaeri.configs.serdes.TwoSideObjectTransformer;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class AdventureTransformer extends TwoSideObjectTransformer<Component, String> {
    private final MiniMessage miniMessage;

    public AdventureTransformer(MiniMessage miniMessage) {
        this.miniMessage = miniMessage;
    }

    @Override
    public GenericsPair<Component, String> getPair() {
        return genericsPair(Component.class, String.class);
    }

    @Override
    public String leftToRight(Component component, SerdesContext serdesContext) {
        return this.miniMessage.serialize(component);
    }

    @Override
    public Component rightToLeft(String s, SerdesContext serdesContext) {
        // supporting migration from legacy color codes to MM syntax.
        // this method first deserializes using legacy component serializer, (& -> Component)
        // then serializes component back to string (now the string contains MM syntax created from legacy colors) (Component -> MM + &)
        // and then again serializes back to component (MM + & -> Component)
        if(MessageConfiguration.containsLegacyColors(s)) {
            return this.miniMessage.parse(this.miniMessage.serialize(LegacyComponentSerializer.legacy('&').deserialize(s)));
        }
        return this.miniMessage.deserialize(s);
    }
}

package net.dzikoysk.funnyguilds.config.serdes;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.bukkit.Color;

public class ColorSerializer implements ObjectSerializer<Color> {

    @Override
    public boolean supports(Class<? super Color> type) {
        return Color.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(Color color, SerializationData data, GenericsDeclaration generics) {
        data.add("red", color.getRed());
        data.add("green", color.getGreen());
        data.add("blue", color.getBlue());
    }

    @Override
    public Color deserialize(DeserializationData data, GenericsDeclaration generics) {
        return Color.fromRGB(
                data.get("red", int.class),
                data.get("green", int.class),
                data.get("blue", int.class)
        );
    }

}

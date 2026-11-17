package net.dzikoysk.funnyguilds.config.serdes;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import java.util.Locale;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

public class TextColorSerializer implements ObjectSerializer<TextColor> {

    @Override
    public boolean supports(@NotNull Class<?> type) {
        return TextColor.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(
            TextColor color,
            SerializationData data,
            GenericsDeclaration generics
    ) {
        if (color instanceof NamedTextColor namedTextColor) {
            data.setValue(namedTextColor.toString());
        } else {
            data.setValue(color.asHexString());
        }
    }

    @Override
    public TextColor deserialize(
            DeserializationData data,
            GenericsDeclaration generics
    ) {
        String value = data.getValue(String.class);
        TextColor textColor = TextColor.fromCSSHexString(value);
        if (textColor != null) {
            return textColor;
        }

        NamedTextColor namedTextColor = NamedTextColor.NAMES.value(value.toLowerCase(Locale.ROOT));
        if (namedTextColor != null) {
            return namedTextColor;
        }
        throw new IllegalArgumentException("Invalid text color: " + value);
    }

}

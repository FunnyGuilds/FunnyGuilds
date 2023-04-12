package net.dzikoysk.funnyguilds.config.serdes;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;

public class ZoneIdTransformer implements ObjectSerializer<ZoneId> {

    @Override
    public boolean supports(@NotNull Class<? super ZoneId> type) {
        return ZoneId.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(ZoneId object, SerializationData data, @NotNull GenericsDeclaration generics) {
        data.add("time-zone", object.getId());
    }

    @Override
    public ZoneId deserialize(DeserializationData data, @NotNull GenericsDeclaration generics) {
        return ZoneId.of(data.get("time-zone", String.class));
    }
}

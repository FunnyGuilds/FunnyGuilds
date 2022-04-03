package net.dzikoysk.funnyguilds.config.serdes;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class VectorSerializer implements ObjectSerializer<Vector> {

    @Override
    public boolean supports(@NotNull Class<? super Vector> type) {
        return Vector.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NotNull Vector object, @NotNull SerializationData data, @NotNull GenericsDeclaration generics) {
        data.add("x", object.getX());
        data.add("y", object.getY());
        data.add("z", object.getZ());
    }

    @Override
    public Vector deserialize(@NotNull DeserializationData data, @NotNull GenericsDeclaration generics) {

        double x = data.get("x", double.class);
        double y = data.get("y", double.class);
        double z = data.get("z", double.class);

        return new Vector(x, y, z);
    }
}
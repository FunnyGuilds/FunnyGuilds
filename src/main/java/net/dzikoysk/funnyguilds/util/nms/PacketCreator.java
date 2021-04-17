package net.dzikoysk.funnyguilds.util.nms;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.apache.commons.lang3.Validate;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class PacketCreator {

    private static final Map<String, ThreadLocal<PacketCreator>> PACKET_CREATOR_CACHE = new HashMap<>();

    private final Class<?>           packetClass;
    private final Constructor<?>     packetConstructor;
    private final Map<String, Field> packetFields;
    private       Object             packetInstance;

    private PacketCreator(Class<?> packetClass) {
        this.packetClass = packetClass;
        this.packetConstructor = Reflections.getConstructor(packetClass);
        this.packetFields = new HashMap<>(this.packetClass.getDeclaredFields().length);

        for (Field field : this.packetClass.getDeclaredFields()) {
            field.setAccessible(true);
            this.packetFields.put(field.getName(), field);
        }
    }

    public static PacketCreator of(String packetClassName) {
        ThreadLocal<PacketCreator> creator = PACKET_CREATOR_CACHE.get(packetClassName);

        if (creator == null) {
            Class<?> packetClass = Reflections.getNMSClass(packetClassName);
            creator = ThreadLocal.withInitial(() -> new PacketCreator(packetClass));
            PACKET_CREATOR_CACHE.put(packetClassName, creator);
        }

        return creator.get();
    }

    public static PacketCreator of(Class<?> packetClass) {
        ThreadLocal<PacketCreator> creator = PACKET_CREATOR_CACHE.get(packetClass.getName());

        if (creator == null) {
            creator = ThreadLocal.withInitial(() -> new PacketCreator(packetClass));
            PACKET_CREATOR_CACHE.put(packetClass.getName(), creator);
        }

        return creator.get();
    }

    public PacketCreator create() {
        try {
            this.packetInstance = this.packetConstructor.newInstance();
        }
        catch (final InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
            FunnyGuilds.getPluginLogger().error("PacketCreator error", exception);
        }

        return this;
    }

    public PacketCreator withField(String fieldName, Object value) {
        Validate.notNull(value, "Value cannot be NULL!");
        if (this.packetInstance == null) {
            throw new RuntimeException("Tried to set field on non-existing packet instance!");
        }

        try {
            Field field = this.packetFields.get(fieldName);
            field.set(this.packetInstance, value);
        }
        catch (final IllegalAccessException ex) {
            FunnyGuilds.getPluginLogger().error("Could not retrieve field from given packet class", ex);
        }

        return this;
    }

    public PacketCreator withField(String fieldName, Object value, Class<?> fieldType) {
        Validate.notNull(value, "Value cannot be NULL!");
        if (this.packetInstance == null) {
            throw new RuntimeException("Tried to set field on non-existing packet instance!");
        }

        try {
            Field field = this.packetFields.get(fieldName);

            if (! fieldType.isAssignableFrom(field.getType())) {
                FunnyGuilds.getPluginLogger().error("Given fieldType is not assignable from found field's type");
            }

            field.set(this.packetInstance, value);
        }
        catch (final IllegalAccessException ex) {
            FunnyGuilds.getPluginLogger().error("Could not retrieve field from given packet class", ex);
        }

        return this;
    }

    public void send(final Collection<? extends Player> players) {
        PacketSender.sendPacket(players, this.getPacket());
    }

    public Object getPacket() {
        return packetInstance;
    }

}

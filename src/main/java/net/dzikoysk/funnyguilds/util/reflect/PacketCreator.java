package net.dzikoysk.funnyguilds.util.reflect;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class PacketCreator {

    private static final Map<String, ThreadLocal<PacketCreator>> PACKET_CREATOR_CACHE = new HashMap<>();

    private final Class<?> packetClass;
    private final Map<String, Field> packetFields;
    private Object packetInstance;

    private PacketCreator(Class<?> packetClass) {
        this.packetClass = packetClass;
        this.packetFields = new HashMap<>(this.packetClass.getDeclaredFields().length);

        for (Field field : this.packetClass.getDeclaredFields()) {
            field.setAccessible(true);
            this.packetFields.put(field.getName(), field);
        }
    }

    public static PacketCreator of(String packetClassName) {
        ThreadLocal<PacketCreator> creator = PACKET_CREATOR_CACHE.get(packetClassName);

        if (creator == null) {
            Class<?> packetClass = Reflections.getCraftClass(packetClassName);
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
            this.packetInstance = this.packetClass.newInstance();
        } catch (final InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
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
        } catch (final IllegalAccessException ex) {
            FunnyGuilds.exception(ex.getMessage(), ex.getStackTrace());
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

            if (!fieldType.isAssignableFrom(field.getType())) {
                FunnyGuilds.error("Given fieldType is not assignable from found field's type");
            }

            field.set(this.packetInstance, value);
        } catch (final IllegalAccessException ex) {
            FunnyGuilds.exception(ex.getMessage(), ex.getStackTrace());
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

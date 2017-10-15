package net.dzikoysk.funnyguilds.util.reflect;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public final class PacketCreator {

    private final Class<?> packetClass;
    private Object packetInstance;

    public static PacketCreator of(Class<?> packetClass) {
        return new PacketCreator(packetClass);
    }

    public static PacketCreator of(Constructor<?> packetConstructor, Object... params) {
        return new PacketCreator(packetConstructor, params);
    }

    private PacketCreator(Class<?> packetClass) {
        this.packetClass = packetClass;

        try {
            this.packetInstance = packetClass.newInstance();
        }
        catch(final InstantiationException | IllegalAccessException ex) {
            FunnyGuilds.exception(ex.getMessage(), ex.getStackTrace());
            this.packetInstance = null;
        }
    }

    private PacketCreator(Constructor<?> packetConstructor, Object... params) {
        this.packetClass = packetConstructor.getDeclaringClass();

        try {
            this.packetInstance = packetConstructor.newInstance(params);
        }
        catch (final InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            FunnyGuilds.exception(ex.getMessage(), ex.getStackTrace());
            this.packetInstance = null;
        }
    }

    public PacketCreator withField(String fieldName, Object value) {
        try
        {
            Field field = this.packetClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(this.packetInstance, value);
        }
        catch(final NoSuchFieldException ex) {
            FunnyGuilds.exception("Could not find field with given fieldName", ex.getStackTrace());
        }
        catch(final IllegalAccessException ex) {
            FunnyGuilds.exception(ex.getMessage(), ex.getStackTrace());
        }

        return this;
    }

    public PacketCreator withField(String fieldName, Object value, Class<?> fieldType) {
        try {
            final Field field = this.packetClass.getDeclaredField(fieldName);

            if (! fieldType.isAssignableFrom(field.getType())) {
                FunnyGuilds.error("Given fieldType is not assignable from found field's type");
            }

            field.setAccessible(true);
            field.set(this.packetInstance, value);
        }
        catch(final NoSuchFieldException ex) {
            FunnyGuilds.exception("Could not find field with given fieldName", ex.getStackTrace());
        }
        catch(final IllegalAccessException ex) {
            FunnyGuilds.exception(ex.getMessage(), ex.getStackTrace());
        }

        return this;
    }

    public void send(final Collection<? extends Player> players) {
        PacketSender.sendPacket(players, this.create());
    }

    public Object create() {
        return packetInstance;
    }
}

package net.dzikoysk.funnyguilds.guild.permission;

import java.util.Objects;
import net.kyori.adventure.key.Key;

final class SimpleGuildPermission<T> implements GuildPermission<T> {
    
    private final Key key;
    private final Class<T> valueType;

    SimpleGuildPermission(Key key, Class<T> valueType) {
        this.key = key;
        this.valueType = valueType;
    }

    @Override
    public Key key() {
        return this.key;
    }

    @Override
    public Class<T> getValueType() {
        return this.valueType;
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SimpleGuildPermission)) {
            return false;
        }
        SimpleGuildPermission<?> that = (SimpleGuildPermission<?>) o;
        return Objects.equals(this.key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.key);
    }

    @Override
    public String toString() {
        return "GuildPermission{key=" + this.key + ", valueType=" + this.valueType.getSimpleName() + "}";
    }
}

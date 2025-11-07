package net.dzikoysk.funnyguilds.guild.permission;

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
}

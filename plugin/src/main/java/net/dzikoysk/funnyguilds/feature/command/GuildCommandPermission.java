package net.dzikoysk.funnyguilds.feature.command;

import net.dzikoysk.funnyguilds.guild.permission.GuildPermission;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;

public enum GuildCommandPermission implements GuildPermission<Boolean> {
    BASE("command.base"),
    SET_BASE("command.set-base"),
    EXTEND_VALIDITY("command.extend-validity"),
    ENLARGE("command.enlarge"),
    INVITE("command.invite"),
    KICK("command.kick"),
    LEAVE("command.leave"),
    DEPUTY("command.deputy"),
    ALLY("command.ally"),
    WAR("command.war"),
    PVP("command.pvp"),
    DELETE("command.delete"),
    PANEL("command.panel");
    
    private final Key key;
    
    GuildCommandPermission(@KeyPattern String key) {
        this.key = Key.key(GuildPermission.PLUGIN_NAMESPACE, key);
    }
    
    @Override
    public Key key() {
        return this.key;
    }

    @Override
    public Class<Boolean> getValueType() {
        return Boolean.class;
    }
}

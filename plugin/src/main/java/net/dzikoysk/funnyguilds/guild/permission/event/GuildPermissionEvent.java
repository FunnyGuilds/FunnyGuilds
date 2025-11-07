package net.dzikoysk.funnyguilds.guild.permission.event;

import java.util.Objects;
import net.dzikoysk.funnyguilds.event.guild.GuildEvent;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.permission.GuildPermission;
import net.dzikoysk.funnyguilds.user.User;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;

public abstract class GuildPermissionEvent extends GuildEvent {

    private final GuildPermission<?> permission;
    private Object permissionValue;

    GuildPermissionEvent(
            EventCause eventCause,
            Guild guild,
            User doer,
            GuildPermission<?> permission
    ) {
        super(
                eventCause,
                Objects.requireNonNull(
                        doer,
                        "Doer cannot be null"
                ),
                guild,
                false
        );
        this.permission = permission;
    }

    @SuppressWarnings("unchecked")
    public <T> GuildPermission<T> getPermission() {
        return (GuildPermission<T>) this.permission;
    }

    @SuppressWarnings("unchecked")
    public <T> Option<T> getPermissionValue() {
        return Option.of((T) this.permissionValue);
    }

    public <T> void setPermissionValue(@Nullable T permissionValue) {
        if (permissionValue != null && !this.permission.getValueType().isInstance(permissionValue)) {
            throw new IllegalArgumentException("Permission value must be of type " +
                                               this.permission.getValueType().getSimpleName());
        }
        this.permissionValue = permissionValue;
    }
}

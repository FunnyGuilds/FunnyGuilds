package net.dzikoysk.funnyguilds.guild.permission.event;

import java.util.Objects;
import net.dzikoysk.funnyguilds.event.guild.GuildEvent;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.permission.GuildPermission;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;

public final class GuildPermissionCheckEvent extends GuildEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final GuildPermission<?> permission;
    private @Nullable Object permissionValue;

    private GuildPermissionCheckEvent(
            EventCause eventCause,
            User doer,
            Guild guild,
            GuildPermission<?> permission
    ) {
        super(
                eventCause,
                Objects.requireNonNull(doer, "Doer cannot be null"),
                guild,
                false
        );
        this.permission = Objects.requireNonNull(permission, "Permission cannot be null");
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
            throw new IllegalArgumentException("Permission value must be of type " + this.permission.getValueType().getSimpleName());
        }
        this.permissionValue = permissionValue;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Checking guild permission " + this.permission + " has been cancelled by the server! Returned default value.";
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public static <T> GuildPermissionCheckEvent of(
            EventCause eventCause, 
            User doer,
            Guild guild,
            GuildPermission<T> permission
    ) {
        return new GuildPermissionCheckEvent(
                eventCause,
                doer,
                guild,
                permission
        );
    }
}

package net.dzikoysk.funnyguilds.guild.permission.event;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.permission.GuildPermission;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;

/**
 * Called when the permission value is being checked (e.g., in command execution).
 */
public class GuildPermissionCheckEvent extends GuildPermissionEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    
    private Object permissionValue;

    public GuildPermissionCheckEvent(
            Guild guild,
            User doer,
            GuildPermission<?> permission
    ) {
        super(
                EventCause.SYSTEM,
                guild,
                doer,
                permission
        );
    }

    @Override
    public Option<?> getPermissionValue() {
        return Option.of(this.permissionValue);
    }

    public <T> void setPermissionValue(@Nullable T permissionValue) {
        if (permissionValue != null && !this.getPermission().getValueType().isInstance(permissionValue)) {
            throw new IllegalArgumentException("Permission value must be of type " +
                                               this.getPermission().getValueType().getSimpleName());
        }
        this.permissionValue = permissionValue;
    }
    
    @Override
    public String getDefaultCancelMessage() {
        throw new UnsupportedOperationException("GuildPermissionCheckEvent cannot be cancelled directly. Use permission value setting instead.");
    }

    @Override
    public void setCancelled(boolean cancelled) {
        throw new UnsupportedOperationException("GuildPermissionCheckEvent cannot be cancelled directly. Use permission value setting instead.");
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

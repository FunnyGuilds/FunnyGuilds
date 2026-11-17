package net.dzikoysk.funnyguilds.guild.permission.event;

import java.util.Objects;
import net.dzikoysk.funnyguilds.event.guild.GuildEvent;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.permission.GuildPermission;
import net.dzikoysk.funnyguilds.user.User;

public abstract class GuildPermissionEvent extends GuildEvent {

    private final GuildPermission<?> permission;
    private panda.std.Result<?, Runnable> permissionResult;

    GuildPermissionEvent(
            EventCause eventCause,
            Guild guild,
            User doer,
            GuildPermission<?> permission,
            panda.std.Result<?, Runnable> permissionResult
    ) {
        super(
                eventCause,
                Objects.requireNonNull(
                        doer,
                        "Doer cannot be null"
                ),
                guild
        );
        this.permission = permission;
        this.permissionResult = permissionResult;
    }
    
    public GuildPermission<?> getPermission() {
        return this.permission;
    }
    
    public panda.std.Result<?, Runnable> getPermissionResult() {
        if (this.permissionResult == null) {
            return panda.std.Result.ok(null);
        }
        return this.permissionResult;
    }
    
    public boolean hasPermissionResult() {
        return this.permissionResult != null;
    }
    
    public void setPermissionResult(panda.std.Result<?, Runnable> permissionResult) {
        this.permissionResult = Objects.requireNonNull(permissionResult, "Permission result cannot be null");
    }
    
    public void setSuccessResult(Object successResult) {
        Objects.requireNonNull(successResult, "Success result cannot be null");

        Class<?> valueType = this.permission.getValueType();
        if (!valueType.isInstance(successResult)) {
            throw new IllegalArgumentException("Permission result must be of type " + valueType.getSimpleName());
        }
        
        this.permissionResult = panda.std.Result.ok(successResult);
    }
    
    public void setErrorResult(Runnable action) {
        this.permissionResult = panda.std.Result.error(Objects.requireNonNull(action, "Action cannot be null"));
    }
    
    @Override
    public String getDefaultCancelMessage() {
        throw new UnsupportedOperationException("GuildPermissionEvent cannot be cancelled directly. Use permission value setting instead.");
    }

    @Override
    public void setCancelled(boolean cancelled) {
        throw new UnsupportedOperationException("GuildPermissionEvent cannot be cancelled directly. Use permission value setting instead.");
    }
}

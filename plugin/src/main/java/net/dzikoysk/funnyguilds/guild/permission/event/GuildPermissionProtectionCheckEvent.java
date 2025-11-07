package net.dzikoysk.funnyguilds.guild.permission.event;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.permission.GuildPermission;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import panda.std.Option;

/**
 * Called when a permission check is performed for protection-related actions (e.g., block breaking, placing).
 */
public class GuildPermissionProtectionCheckEvent extends GuildPermissionEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Reference<Event> internalEventReference;

    public GuildPermissionProtectionCheckEvent(
            Guild guild,
            User doer,
            GuildPermission<Boolean> permission,
            Event event
    ) {
        super(
                GuildPermissionEvent.EventCause.SYSTEM,
                guild,
                doer,
                permission
        );
        this.internalEventReference = new WeakReference<>(event);
    }

    @SuppressWarnings("unchecked")
    @Override
    public GuildPermission<Boolean> getPermission() {
        return (GuildPermission<Boolean>) super.getPermission();
    }

    @Override
    public panda.std.Result<Boolean, Runnable> getPermissionResult() {
        return super.getPermissionResult()
                .map(result -> (Boolean) result);
    }

    /**
     * @return the internal event related to the protection action, if available
     */
    public Option<Event> getInternalEvent() {
        return Option.of(this.internalEventReference.get());
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}

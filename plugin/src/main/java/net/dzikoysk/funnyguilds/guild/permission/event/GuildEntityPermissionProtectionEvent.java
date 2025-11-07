package net.dzikoysk.funnyguilds.guild.permission.event;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.permission.GuildPermission;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;
import panda.std.Option;

public final class GuildEntityPermissionProtectionEvent extends GuildPermissionProtectionEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Reference<EntityEvent> entityEventReference;

    public GuildEntityPermissionProtectionEvent(
            EventCause eventCause,
            Guild guild,
            User doer,
            GuildPermission<?> permission,
            EntityEvent entityEvent
    ) {
        super(
                eventCause,
                guild,
                doer,
                permission,
                entityEvent.getEntity().getLocation()
        );
        this.entityEventReference = new WeakReference<>(entityEvent);
    }

    @Override
    public Option<EntityEvent> getInternalEvent() {
        return Option.of(this.entityEventReference.get());
    }

    @Override
    public String getDefaultCancelMessage() {
        Guild guild = this.getGuild();
        User doer = this.getDoer().orThrow(() -> new IllegalStateException("Doer cannot be null"));
        return "[FunnyGuilds] Checking guild permission " +
               this.getPermission() +
               " for guild " +
               guild.getName() +
               " by user " +
               doer.getName() +
               " for entity protection at location " +
               this.getLocation() +
               " has been cancelled by the server!";
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}

package net.dzikoysk.funnyguilds.guild.permission.event;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.permission.GuildPermission;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;
import org.bukkit.event.hanging.HangingEvent;
import org.jetbrains.annotations.NotNull;
import panda.std.Option;

public final class GuildHangingPermissionProtectionEvent extends GuildPermissionProtectionEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Reference<HangingEvent> hangingEventReference;

    public GuildHangingPermissionProtectionEvent(
            EventCause eventCause,
            Guild guild,
            User doer,
            GuildPermission<?> permission,
            HangingEvent hangingEvent
    ) {
        super(
                eventCause,
                guild,
                doer,
                permission,
                hangingEvent.getEntity().getLocation()
        );
        this.hangingEventReference = new WeakReference<>(hangingEvent);
    }

    @Override
    public Option<HangingEvent> getInternalEvent() {
        return Option.of(this.hangingEventReference.get());
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
               " for hanging protection at location " +
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

package net.dzikoysk.funnyguilds.guild.permission.event;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.permission.GuildPermission;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.jetbrains.annotations.NotNull;
import panda.std.Option;

public final class GuildBlockPermissionProtectionEvent extends GuildPermissionProtectionEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Reference<BlockEvent> blockEventReference;

    public GuildBlockPermissionProtectionEvent(
            EventCause eventCause,
            Guild guild,
            User doer,
            GuildPermission<?> permission,
            BlockEvent blockEvent
    ) {
        super(
                eventCause,
                guild,
                doer,
                permission,
                blockEvent.getBlock().getLocation()
        );
        this.blockEventReference = new WeakReference<>(blockEvent);
    }

    @Override
    public Option<BlockEvent> getInternalEvent() {
        return Option.of(this.blockEventReference.get());
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
               " for block protection at location " +
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

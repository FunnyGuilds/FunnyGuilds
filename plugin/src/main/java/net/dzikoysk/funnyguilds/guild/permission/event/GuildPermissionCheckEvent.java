package net.dzikoysk.funnyguilds.guild.permission.event;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.permission.GuildPermission;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when the permission value is being checked (e.g., in command execution).
 */
public class GuildPermissionCheckEvent extends GuildPermissionEvent {

    private static final HandlerList HANDLERS = new HandlerList();

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
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

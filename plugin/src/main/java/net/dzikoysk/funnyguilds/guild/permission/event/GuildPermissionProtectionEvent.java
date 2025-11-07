package net.dzikoysk.funnyguilds.guild.permission.event;

import java.util.Objects;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.permission.GuildPermission;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Location;
import org.bukkit.event.Event;
import panda.std.Option;

/**
 * Called when a permission check is performed for protection-related actions (e.g., block breaking, placing).
 */
public abstract class GuildPermissionProtectionEvent extends GuildPermissionEvent {
    
    private final Location location;
    
    GuildPermissionProtectionEvent(
            EventCause eventCause,
            Guild guild,
            User doer,
            GuildPermission<?> permission,
            Location location
    ) {
        super(
                eventCause,
                guild,
                doer,
                permission
        );
        this.location = Objects.requireNonNull(location, "Location cannot be null");
    }

    /**
     * @return the location related to the protection action
     */
    public Location getLocation() {
        return this.location;
    }

    /**
     * @return the internal event related to the protection action, if available
     */
    public abstract Option<? extends Event> getInternalEvent();
}

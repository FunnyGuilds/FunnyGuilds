package net.dzikoysk.funnyguilds.guild.permission.event;

import java.util.Objects;
import net.dzikoysk.funnyguilds.event.guild.GuildEvent;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.permission.GuildPermission;
import net.dzikoysk.funnyguilds.user.User;
import panda.std.Option;

public abstract class GuildPermissionEvent extends GuildEvent {

    private final GuildPermission<?> permission;

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
    
    public GuildPermission<?> getPermission() {
        return this.permission;
    }
    
    public abstract Option<?> getPermissionValue();
}

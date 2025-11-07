package net.dzikoysk.funnyguilds.guild.permission;

import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.permission.event.GuildPermissionCheckEvent;
import net.dzikoysk.funnyguilds.guild.permission.event.GuildPermissionProtectionCheckEvent;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.Event;
import panda.std.Option;

public class EventGuildPermissionController implements GuildPermissionController {

    @SuppressWarnings("unchecked")
    @Override
    public <T> Option<T> getPermissionValue(
            Guild guild,
            User user,
            GuildPermission<T> permission
    ) {
        GuildPermissionCheckEvent permissionEvent = new GuildPermissionCheckEvent(
                guild,
                user,
                permission
        );
        SimpleEventHandler.handle(permissionEvent);
        return (Option<T>) permissionEvent.getPermissionValue();
    }

    @Override
    public Option<Boolean> getProtectionPermissionValue(
            Guild guild,
            User user,
            GuildPermission<Boolean> permission,
            Event event
    ) {
        GuildPermissionProtectionCheckEvent protectionEvent = new GuildPermissionProtectionCheckEvent(
                guild,
                user,
                permission,
                event
        );
        boolean wasNotCancelled = SimpleEventHandler.handle(protectionEvent);

        GuildPermissionCheckEvent permissionEvent = new GuildPermissionCheckEvent(
                guild,
                user,
                permission
        );
        permissionEvent.setPermissionValue(wasNotCancelled);
        SimpleEventHandler.handle(permissionEvent);

        return permissionEvent.getPermissionValue().is(Boolean.class);
    }

}

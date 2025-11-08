package net.dzikoysk.funnyguilds.guild.permission;

import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.permission.event.GuildPermissionCheckEvent;
import net.dzikoysk.funnyguilds.guild.permission.event.GuildPermissionEvent;
import net.dzikoysk.funnyguilds.guild.permission.event.GuildPermissionProtectionCheckEvent;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.Event;
import panda.std.Option;
import panda.std.Result;

final class EventGuildPermissionController implements GuildPermissionController {
    
    static final Runnable EMPTY_ERROR_ACTION = () -> {
    };
    
    EventGuildPermissionController() {
    }

    @Override
    public <T> Option<T> getPermissionValue(
            Guild guild,
            User user,
            GuildPermission<T> permission
    ) {
        return this.getPermissionResult(
                guild,
                user,
                permission,
                null,
                null
        ).toOption();
    }

    @Override
    public Result<Boolean, Runnable> getPermissionResult(
            Guild guild,
            User user,
            GuildPermission<Boolean> permission
    ) {
        return this.getPermissionResult(
                guild,
                user,
                permission,
                null,
                EMPTY_ERROR_ACTION
        );
    }

    @Override
    public Result<Boolean, Runnable> getProtectionPermissionResult(
            Guild guild,
            User user,
            GuildPermission<Boolean> permission,
            Event event
    ) {
        return this.getProtectionPermissionResult(
                guild,
                user,
                permission,
                event,
                EMPTY_ERROR_ACTION
        );
    }
    
    <T> Result<T, Runnable> getPermissionResult(
            Guild guild,
            User user,
            GuildPermission<T> permission,
            Result<T, Runnable> permissionResult,
            Runnable failureAction
    ) {
        return handleAndReturn(
                new GuildPermissionCheckEvent(
                        guild,
                        user,
                        permission,
                        permissionResult
                ))
                .flatMap(GuildPermissionEvent::getPermissionResult)
                .is(permission.getValueType(), value -> failureAction);
    }
    
     Result<Boolean, Runnable> getProtectionPermissionResult(
            Guild guild,
            User user,
            GuildPermission<Boolean> permission,
            Event event,
            Runnable failureAction
    ) {
        return handleAndReturn(
                new GuildPermissionProtectionCheckEvent(
                        guild,
                        user,
                        permission,
                        event
                ))
                .flatMap(protectionEvent -> this.getPermissionResult(
                        guild,
                        user,
                        permission,
                        protectionEvent.getPermissionResult(),
                        failureAction
                ));
    }
     
    static <E extends GuildPermissionEvent> Result<E, Runnable> handleAndReturn(E event) {
        return Result.<E, Runnable>ok(event).peek(SimpleEventHandler::handle);
    }
}

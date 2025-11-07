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

public class EventGuildPermissionController implements GuildPermissionController {

    private static final Runnable EMPTY_ACTION = () -> {
    };

    @Override
    public <T> Option<T> getPermissionValue(
            Guild guild,
            User user,
            GuildPermission<T> permission
    ) {
        return handleAndReturnResult(
                new GuildPermissionCheckEvent(
                        guild,
                        user,
                        permission,
                        null
                ), permission)
                .mapErr(ignored -> null)
                .toOption();
    }

    @Override
    public Result<Boolean, Runnable> getPermissionResult(
            Guild guild,
            User user,
            GuildPermission<Boolean> permission
    ) {
        return handleAndReturnResult(
                new GuildPermissionCheckEvent(
                        guild,
                        user,
                        permission,
                        null
                ),
                permission
        );
    }

    @Override
    public Result<Boolean, Runnable> getProtectionPermissionResult(
            Guild guild,
            User user,
            GuildPermission<Boolean> permission,
            Event event
    ) {
        return handleAndReturn(
                new GuildPermissionProtectionCheckEvent(
                        guild,
                        user,
                        permission,
                        event
                ))
                .flatMap(protectionEvent -> handleAndReturnResult(
                        new GuildPermissionCheckEvent(
                                guild,
                                user,
                                permission,
                                protectionEvent.getPermissionResult()
                        ),
                        permission
                ));
    }

    private static Result<GuildPermissionEvent, Runnable> handleAndReturn(GuildPermissionEvent event) {
        return Result
                .<GuildPermissionEvent, Runnable>ok(event)
                .map(SimpleEventHandler::handleAndReturn);
    }

    private static <T> Result<T, Runnable> handleAndReturnResult(
            GuildPermissionEvent event,
            GuildPermission<T> permission
    ) {
        return handleAndReturn(event)
                .flatMap(GuildPermissionEvent::getPermissionResult)
                .is(permission.getValueType(), value -> EMPTY_ACTION);
    }

}

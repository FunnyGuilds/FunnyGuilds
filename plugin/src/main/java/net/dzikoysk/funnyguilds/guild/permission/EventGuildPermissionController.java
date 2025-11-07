package net.dzikoysk.funnyguilds.guild.permission;

import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.permission.event.GuildPermissionCheckEvent;
import net.dzikoysk.funnyguilds.guild.permission.event.GuildPermissionProtectionCheckEvent;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.Event;
import panda.std.Option;
import panda.std.Result;

public class EventGuildPermissionController implements GuildPermissionController {

    @SuppressWarnings("unchecked")
    @Override
    public <T> Option<T> getPermissionValue(
            Guild guild,
            User user,
            GuildPermission<T> permission
    ) {
        GuildPermissionCheckEvent event = new GuildPermissionCheckEvent(
                guild,
                user,
                permission
        );
        SimpleEventHandler.handle(event);
        return event.getPermissionResult()
                .map(result -> (T) result)
                .toOption();
    }

    @Override
    public Result<Boolean, Runnable> getPermissionResult(
            Guild guild,
            User user,
            GuildPermission<Boolean> permission
    ) {
        GuildPermissionCheckEvent event = new GuildPermissionCheckEvent(
                guild,
                user,
                permission
        );
        SimpleEventHandler.handle(event);
        return event.getPermissionResult()
                .map(result -> (Boolean) result);
    }

    @Override
    public Result<Boolean, Runnable> getProtectionPermissionResult(
            Guild guild,
            User user,
            GuildPermission<Boolean> permission,
            Event event
    ) {
        return Result.<GuildPermissionProtectionCheckEvent, Runnable>ok(new GuildPermissionProtectionCheckEvent(
                        guild,
                        user,
                        permission,
                        event
                ))
                .peek(SimpleEventHandler::handle)
                .map(EventGuildPermissionController::toCheckEvent)
                .peek(SimpleEventHandler::handle)
                .flatMap(GuildPermissionCheckEvent::getPermissionResult)
                .map(result -> (Boolean) result);
    }
    
    private static GuildPermissionCheckEvent toCheckEvent(GuildPermissionProtectionCheckEvent protectionEvent) {
        GuildPermissionCheckEvent checkEvent = new GuildPermissionCheckEvent(
                protectionEvent.getGuild(),
                protectionEvent.getDoer().get(),
                protectionEvent.getPermission()
        );
        checkEvent.setPermissionResult(protectionEvent.getPermissionResult());
        return checkEvent;
    }

}

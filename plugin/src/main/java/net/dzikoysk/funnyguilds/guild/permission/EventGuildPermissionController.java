package net.dzikoysk.funnyguilds.guild.permission;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.permission.event.GuildPermissionCheckEvent;
import net.dzikoysk.funnyguilds.guild.permission.event.GuildPermissionEvent;
import net.dzikoysk.funnyguilds.guild.permission.event.GuildPermissionProtectionCheckEvent;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import panda.std.Result;

final class EventGuildPermissionController implements GuildPermissionController {

    private final StaticGuildPermissionController staticController;

    EventGuildPermissionController(StaticGuildPermissionController staticController) {
        this.staticController = staticController;
    }

    @Override
    public <T> Result<T, Runnable> getPermissionResult(
            Guild guild,
            User user,
            GuildPermission<T> permission
    ) {
        Result<T, Runnable> staticResult = this.staticController.getPermissionResult(
                guild,
                user,
                permission
        );

        return this.handlePermissionCheck(
                guild,
                user,
                permission,
                staticResult
        );
    }

    @Override
    public Result<Boolean, Runnable> getProtectionPermissionResult(
            Guild guild,
            User user,
            GuildPermission<Boolean> permission,
            Event event
    ) {
        Result<Boolean, Runnable> staticResult = this.staticController.getProtectionPermissionResult(
                guild,
                user,
                permission,
                event
        );

        Result<Boolean, Runnable> protectionResult = handleAndReturn(
                guild,
                user,
                permission,
                new GuildPermissionProtectionCheckEvent(
                        guild,
                        user,
                        permission,
                        event,
                        staticResult
                )
        );

        return this.handlePermissionCheck(
                guild,
                user,
                permission,
                protectionResult
        );
    }

    private <T> Result<T, Runnable> handlePermissionCheck(
            Guild guild,
            User user,
            GuildPermission<T> permission,
            @Nullable Result<T, Runnable> permissionResult
    ) {
        return handleAndReturn(
                guild,
                user,
                permission,
                new GuildPermissionCheckEvent(
                        guild,
                        user,
                        permission,
                        permissionResult
                )
        );
    }

    static <T> Result<T, Runnable> handleAndReturn(
            Guild guild,
            User user,
            GuildPermission<T> permission,
            GuildPermissionEvent event
    ) {
        return Result.<GuildPermissionEvent, Runnable>ok(event)
                .peek(SimpleEventHandler::handle)
                .flatMap(GuildPermissionEvent::getPermissionResult)
                .map(permission.getValueType()::cast)
                .mapErr(errorAction -> () -> {
                    if (errorAction != null) {
                        errorAction.run();
                    }
                    FunnyGuilds.getPluginLogger()
                            .debug(String.format(
                                    "No permission result for permission '%s' in guild '%s' and user '%s'",
                                    permission,
                                    guild.getName(),
                                    user.getName()
                            ));
                });
    }

}

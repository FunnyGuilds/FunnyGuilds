package net.dzikoysk.funnyguilds.guild.permission;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.Event;
import panda.std.Option;
import panda.std.Result;

final class MixedGuildPermissionController implements GuildPermissionController {

    private final StaticGuildPermissionController staticController;
    private final EventGuildPermissionController eventController;

    MixedGuildPermissionController(
            StaticGuildPermissionController staticController,
            EventGuildPermissionController eventController
    ) {
        this.staticController = staticController;
        this.eventController = eventController;
    }

    @Override
    public <T> Option<T> getPermissionValue(
            Guild guild,
            User user,
            GuildPermission<T> permission
    ) {
        return this.eventController.getPermissionValue(
                        guild,
                        user,
                        permission
                )
                .orElse(() -> this.staticController.getPermissionValue(
                        guild,
                        user,
                        permission
                ));
    }

    @Override
    public Result<Boolean, Runnable> getPermissionResult(
            Guild guild,
            User user,
            GuildPermission<Boolean> permission
    ) {
        return this.eventController.getPermissionResult(
                        guild,
                        user,
                        permission,
                        null,
                        null
                )
                .orElse(handleError(() -> this.staticController.getPermissionResult(
                        guild,
                        user,
                        permission
                )));
    }

    @Override
    public Result<Boolean, Runnable> getProtectionPermissionResult(
            Guild guild,
            User user,
            GuildPermission<Boolean> permission,
            Event event
    ) {
        return this.eventController.getProtectionPermissionResult(
                        guild,
                        user,
                        permission,
                        event,
                        null
                )
                .orElse(handleError(() -> this.staticController.getProtectionPermissionResult(
                        guild,
                        user,
                        permission,
                        event
                )));
    }

    private static Function<Runnable, Result<Boolean, Runnable>> handleError(Supplier<Result<Boolean, Runnable>> fallbackResult) {
        return action -> {
            if (!Objects.equals(
                    EventGuildPermissionController.EMPTY_ERROR_ACTION,
                    action
            )) {
                return Result.error(action);
            }
            return fallbackResult.get();
        };
    }

}

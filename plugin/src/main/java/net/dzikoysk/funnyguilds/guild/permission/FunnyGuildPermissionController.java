package net.dzikoysk.funnyguilds.guild.permission;

import net.dzikoysk.funnyguilds.event.FunnyEvent;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.permission.event.GuildPermissionCheckEvent;
import net.dzikoysk.funnyguilds.user.User;
import panda.std.Option;

public class FunnyGuildPermissionController implements GuildPermissionController {

    @Override
    public <T> Option<T> getPermissionValue(Guild guild, User user, GuildPermission<T> permission) {
        GuildPermissionCheckEvent event = GuildPermissionCheckEvent.of(FunnyEvent.EventCause.SYSTEM, user, guild, permission);
        if (!SimpleEventHandler.handle(event)) {
            return Option.none();
        }
        return event.getPermissionValue();
    }
    
}

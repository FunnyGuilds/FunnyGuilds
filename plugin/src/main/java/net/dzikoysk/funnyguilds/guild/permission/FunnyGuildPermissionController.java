package net.dzikoysk.funnyguilds.guild.permission;

import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.permission.event.GuildBlockPermissionProtectionEvent;
import net.dzikoysk.funnyguilds.guild.permission.event.GuildBucketPermissionProtectionEvent;
import net.dzikoysk.funnyguilds.guild.permission.event.GuildEntityPermissionProtectionEvent;
import net.dzikoysk.funnyguilds.guild.permission.event.GuildHangingPermissionProtectionEvent;
import net.dzikoysk.funnyguilds.guild.permission.event.GuildPermissionCheckEvent;
import net.dzikoysk.funnyguilds.guild.permission.event.GuildPermissionEvent;
import net.dzikoysk.funnyguilds.guild.permission.event.GuildPermissionProtectionEvent;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.hanging.HangingEvent;
import org.bukkit.event.player.PlayerBucketEvent;
import panda.std.Option;

public class FunnyGuildPermissionController implements GuildPermissionController {

    @Override
    public <T> Option<T> getPermissionValue(Guild guild, User user, GuildPermission<T> permission) {
        GuildPermissionCheckEvent event = new GuildPermissionCheckEvent(guild, user, permission);
        if (!SimpleEventHandler.handle(event)) {
            return Option.none();
        }
        return event.getPermissionValue();
    }

    @Override
    public <T> Option<T> getProtectionPermissionValue(Guild guild, User user, GuildPermission<T> permission, Event event) {
        GuildPermissionProtectionEvent protectionEvent;
        if (event instanceof BlockEvent) {
            protectionEvent = new GuildBlockPermissionProtectionEvent(
                    GuildPermissionEvent.EventCause.SYSTEM,
                    guild,
                    user,
                    permission,
                    (BlockEvent) event
            );
        } else if (event instanceof PlayerBucketEvent) {
            protectionEvent = new GuildBucketPermissionProtectionEvent(
                    GuildPermissionEvent.EventCause.SYSTEM,
                    guild,
                    user,
                    permission,
                    (PlayerBucketEvent) event
            );
        } else if (event instanceof HangingEvent) {
            protectionEvent = new GuildHangingPermissionProtectionEvent(
                    GuildPermissionEvent.EventCause.SYSTEM,
                    guild,
                    user,
                    permission,
                    (HangingEvent) event
            );
        } else if (event instanceof EntityEvent) {
            protectionEvent = new GuildEntityPermissionProtectionEvent(
                    GuildPermissionEvent.EventCause.SYSTEM,
                    guild,
                    user,
                    permission,
                    (EntityEvent) event
            );
        } else {
            throw new IllegalArgumentException("Unsupported event type: " + event.getClass().getName());
        }
        
        if (!SimpleEventHandler.handle(protectionEvent)) {
            return Option.none();
        }
        return protectionEvent.getPermissionValue();
    }
}

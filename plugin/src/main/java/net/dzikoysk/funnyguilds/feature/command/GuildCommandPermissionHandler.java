package net.dzikoysk.funnyguilds.feature.command;

import java.util.Collection;
import java.util.EnumSet;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.permission.GuildPermission;
import net.dzikoysk.funnyguilds.guild.permission.event.GuildPermissionCheckEvent;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class GuildCommandPermissionHandler implements Listener {

    private static final Collection<GuildCommandPermission> MEMBER_PERMISSIONS = EnumSet.of(
            GuildCommandPermission.BASE,
            GuildCommandPermission.LEAVE
    );
    private static final Collection<GuildCommandPermission> MANAGER_PERMISSIONS = EnumSet.of(
            GuildCommandPermission.SET_BASE,
            GuildCommandPermission.ENLARGE,
            GuildCommandPermission.EXTEND_VALIDITY,
            GuildCommandPermission.INVITE,
            GuildCommandPermission.KICK,
            GuildCommandPermission.PVP
    );
    private static final Collection<GuildCommandPermission> OWNER_PERMISSIONS = EnumSet.of(
            GuildCommandPermission.DEPUTY,
            GuildCommandPermission.ALLY,
            GuildCommandPermission.WAR,
            GuildCommandPermission.DELETE
    );

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGuildPermissionCheck(GuildPermissionCheckEvent event) {
        GuildPermission<?> permission = event.getPermission();
        if (!(permission instanceof GuildCommandPermission)) {
            return;
        }

        Guild guild = event.getGuild();
        User user = event.getDoer().get();
        if (MEMBER_PERMISSIONS.contains(permission)) {
            event.setPermissionValue(guild.isMember(user));
        }
        else if (MANAGER_PERMISSIONS.contains(permission)) {
            event.setPermissionValue(guild.isDeputy(user) || guild.isOwner(user));
        }
        else if (OWNER_PERMISSIONS.contains(permission)) {
            event.setPermissionValue(guild.isOwner(user));
        }
    }

}

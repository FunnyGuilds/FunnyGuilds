package net.dzikoysk.funnyguilds.guild.permission;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.guild.permission.event.GuildPermissionCheckEvent;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.panda_lang.utilities.inject.annotations.Inject;

public class GenericGuildPermissionHandler implements Listener {

    private static final Collection<GuildPermission<Boolean>> GUILD_CHAT_PERMISSIONS = new HashSet<>(
            Arrays.asList(
                    GenericGuildPermissions.GUILD_CHAT_USE,
                    GenericGuildPermissions.GUILD_CHAT_SEE,
                    GenericGuildPermissions.ALLY_CHAT_USE,
                    GenericGuildPermissions.ALLY_CHAT_SEE,
                    GenericGuildPermissions.GLOBAL_CHAT_USE,
                    GenericGuildPermissions.GLOBAL_CHAT_SEE
            )
    );

    @Inject
    private PluginConfiguration pluginConfiguration;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGuildPermissionCheck(GuildPermissionCheckEvent event) {
        GuildPermission<?> permission = event.getPermission();

        if (GenericGuildPermissions.USER_POSITION.equals(permission)) {
            User user = event.getDoer().get();
            if (user.isOwner()) {
                event.setPermissionValue(this.pluginConfiguration.chatPositionLeader.getValue());
            }
            else if (user.isDeputy()) {
                event.setPermissionValue(this.pluginConfiguration.chatPositionDeputy.getValue());
            }
            else {
                event.setPermissionValue(this.pluginConfiguration.chatPositionMember.getValue());
            }
        } else if (GUILD_CHAT_PERMISSIONS.contains(permission)) {
            event.setPermissionValue(true);
        }
    }

}

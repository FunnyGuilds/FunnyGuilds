package net.dzikoysk.funnyguilds.guild.permission;

import dev.peri.yetanothermessageslibrary.message.Sendable;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.config.message.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.feature.command.GuildCommandPermission;
import net.dzikoysk.funnyguilds.feature.protection.GuildProtectionPermission;
import net.dzikoysk.funnyguilds.guild.permission.event.GuildPermissionCheckEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.panda_lang.utilities.inject.annotations.Inject;

public class GuildPermissionMessageHandler implements Listener {
    
    @Inject
    private MessageService messageService;
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onGuildPermissionCheckMonitor(GuildPermissionCheckEvent event) {
        event.getPermissionValue()
                .is(Boolean.class)
                .filterNot(hasPermission -> hasPermission)
                .map(ignored -> event.getPermission())
                .<Function<MessageConfiguration, Sendable>>map(permission -> {
                    if (permission instanceof GuildProtectionPermission) {
                        return config -> config.regionUnauthorized;
                    } else if (permission instanceof GuildCommandPermission) {
                        return config -> config.generalInsufficientGuildPermission;
                    } else if (GenericGuildPermissions.GUILD_CHAT_USE.equals(permission)) {
                        return config -> config.guildChatPrivateInsufficientPermission;
                    } else if (GenericGuildPermissions.ALLY_CHAT_USE.equals(permission)) {
                        return config -> config.guildChatAlliesInsufficientPermission;
                    } else if (GenericGuildPermissions.GLOBAL_CHAT_USE.equals(permission)) {
                        return config -> config.guildChatGlobalInsufficientPermission;
                    } 
                    return null;
                })
                .peek(messageSupplier -> this.messageService.getMessage(messageSupplier)
                        .receiver(event.getDoer())
                        .send());
    }
}

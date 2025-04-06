package net.dzikoysk.funnyguilds.listener.chat;

import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class SpigotPlayerChat extends PlayerChat {

    @Override
    protected boolean shouldHandleGuildChat() {
        return this.config.guildChatMessageHandler == GuildChatMessageHandler.SPIGOT;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        this.userManager.findByPlayer(event.getPlayer()).peek(user -> {
            if (this.handleGuildChat(event.getPlayer(), user, event.getMessage())) {
                event.setCancelled(true);
                return;
            }

            FunnyFormatter chatMessageFormatter = buildChatMessageFormatter(user);
            event.setFormat(chatMessageFormatter.replace(event.getFormat()));
        });
    }
}

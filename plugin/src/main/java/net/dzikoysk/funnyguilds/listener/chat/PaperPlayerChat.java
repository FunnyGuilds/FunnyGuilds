package net.dzikoysk.funnyguilds.listener.chat;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.regex.Pattern;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class PaperPlayerChat extends PlayerChat {

    private static final Pattern URL_PATTERN = Pattern.compile("((?:(?:https?)://)?[\\w-_\\.]{2,})\\.([a-zA-Z]{2,3}(?:/\\S+)?)");
    private final LegacyComponentSerializer legacySerializer;

    protected PaperPlayerChat() {
        this.legacySerializer = LegacyComponentSerializer.builder()
                .flattener(ComponentFlattener.basic())
                .extractUrls(URL_PATTERN)
                .useUnusualXRepeatedCharacterHexFormat()
                .hexColors()
                .build();
    }

    @Override
    protected boolean shouldHandleGuildChat() {
        return this.config.guildChatMessageHandler == GuildChatMessageHandler.PAPER;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent event) {
        this.userManager.findByPlayer(event.getPlayer()).peek(user -> {
            String stringMessage = this.legacySerializer.serialize(event.message());
            if (this.handleGuildChat(event.getPlayer(), user, stringMessage)) {
                event.setCancelled(true);
                return;
            }

            event.renderer(buildChatRenderer(event.renderer(), user));
        });
    }

    private ChatRenderer buildChatRenderer(ChatRenderer currentRenderer, User user) {
        return (player, displayName, message, viewer) -> {
            Component baseMessage = currentRenderer.render(player, displayName, message, viewer);
            FunnyFormatter chatMessageFormatter = buildChatMessageFormatter(user);
            return chatMessageFormatter.replace(baseMessage);
        };
    }
}

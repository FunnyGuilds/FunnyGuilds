package net.dzikoysk.funnyguilds.feature.panel;

import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import org.bukkit.plugin.Plugin;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public final class GuildRoleNameInput implements Listener {

    private static final long TIMEOUT_TICKS = 30 * 20;

    private final Plugin plugin;
    private final Map<UUID, PendingInput> inputs = new ConcurrentHashMap<>();

    public GuildRoleNameInput(Plugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player, Consumer<String> callback, Runnable onTimeout) {
        UUID playerId = player.getUniqueId();
        PendingInput input = new PendingInput(callback, new AtomicBoolean());
        this.inputs.put(playerId, input);
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
            if (input.claimed().compareAndSet(false, true) && this.inputs.remove(playerId, input)) {
                onTimeout.run();
            }
        }, TIMEOUT_TICKS);
    }

    public void cancel(UUID playerId) {
        this.inputs.remove(playerId);
    }

    public void clear() {
        this.inputs.clear();
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        PendingInput input = this.inputs.get(playerId);
        if (input == null || !input.claimed().compareAndSet(false, true)) {
            return;
        }

        event.setCancelled(true);
        String name = PlainTextComponentSerializer.plainText().serialize(event.originalMessage()).strip();
        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
            if (this.inputs.remove(playerId, input)) {
                input.callback().accept(name);
            }
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.cancel(event.getPlayer().getUniqueId());
    }

    private record PendingInput(Consumer<String> callback, AtomicBoolean claimed) {
    }
}

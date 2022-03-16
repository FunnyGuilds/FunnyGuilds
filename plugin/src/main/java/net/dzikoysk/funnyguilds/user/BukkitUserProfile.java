package net.dzikoysk.funnyguilds.user;

import java.lang.ref.WeakReference;
import java.util.UUID;
import net.dzikoysk.funnyguilds.feature.hooks.vault.VaultHook;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.PingUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import panda.std.Option;

public class BukkitUserProfile implements UserProfile {

    private final UUID uuid;
    private final Server server;
    private final OfflinePlayer offlinePlayer;
    private WeakReference<Player> playerRef;

    public BukkitUserProfile(UUID uuid, Server server) {
        this.uuid = uuid;
        this.server = server;
        this.playerRef = new WeakReference<>(server.getPlayer(this.uuid));
        this.offlinePlayer = server.getOfflinePlayer(this.uuid);
    }

    private Option<Player> getPlayer() {
        Player player = this.playerRef.get();

        if (player == null) {
            player = this.server.getPlayer(this.uuid);

            if (player != null) {
                this.playerRef = new WeakReference<>(player);
            }
        }

        return Option.of(player);
    }

    @Override
    public boolean isOnline() {
        return this.getPlayer().isPresent();
    }

    @Override
    public boolean isVanished() {
        // Should work with VanishNoPacket, SuperVanish and PremiumVanish
        return this.getPlayer()
                .map(player -> player.getMetadata("vanished"))
                .map(metadata -> metadata.stream().anyMatch(MetadataValue::asBoolean))
                .orElseGet(false);
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.getPlayer()
                .map(player -> player.hasPermission(permission))
                .orElseGet(offlinePlayer.isOp() || (VaultHook.isPermissionHooked() && VaultHook.hasPermission(offlinePlayer, permission)));
    }

    @Override
    public int getPing() {
        return getPlayer()
                .map(PingUtils::getPing)
                .orElseGet(0);
    }

    @Override //TODO: MiniMessage support
    public void sendMessage(String message) {
        if (message == null || message.isEmpty()) {
            return;
        }

        this.getPlayer().peek(player -> player.sendMessage(ChatUtils.colored(message)));
    }

    @Override
    public void kick(String reason) {
        this.getPlayer().peek(player -> player.kickPlayer(reason));
    }

}

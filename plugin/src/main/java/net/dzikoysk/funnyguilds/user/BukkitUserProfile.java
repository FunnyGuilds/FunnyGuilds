package net.dzikoysk.funnyguilds.user;

import java.lang.ref.WeakReference;
import java.util.UUID;
import net.dzikoysk.funnyguilds.feature.hooks.vault.VaultHook;
import net.dzikoysk.funnyguilds.shared.Position;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.PingUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.PositionConverter;
import org.apache.commons.lang3.Validate;
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

    public void updateReference(Player player) {
        Validate.notNull(player, "you can't update reference with null player!");
        this.playerRef = new WeakReference<>(player);
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
                .orElseGet(this.offlinePlayer.isOp() || (VaultHook.isPermissionHooked() && VaultHook.hasPermission(this.offlinePlayer, permission)));
    }

    @Override
    public int getPing() {
        return this.getPlayer().map(PingUtils::getPing).orElseGet(0);
    }

    @Override //TODO: MiniMessage support
    public void sendMessage(String message) {
        if (message == null || message.isEmpty()) {
            return;
        }

        this.getPlayer().peek(player -> ChatUtils.sendMessage(player, message));
    }

    @Override
    public void kick(String reason) {
        this.getPlayer().peek(player -> player.kickPlayer(reason));
    }

    @Override
    public void teleport(Position position) {
        this.getPlayer().peek(player -> player.teleport(PositionConverter.adapt(position)));
    }

    @Override
    public Position getPosition() {
        return this.getPlayer()
                .map(Player::getLocation)
                .map(PositionConverter::adapt)
                .orElseGet(Position.ZERO);
    }

}

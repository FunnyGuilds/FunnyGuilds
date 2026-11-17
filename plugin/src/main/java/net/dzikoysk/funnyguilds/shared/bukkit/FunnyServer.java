package net.dzikoysk.funnyguilds.shared.bukkit;

import java.util.Optional;
import java.util.UUID;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class FunnyServer {

    private final Server server;

    public FunnyServer(Server server) {
        this.server = server;
    }

    public Server getServer() {
        return this.server;
    }

    public OfflinePlayer getOfflinePlayer(UUID uuid) {
        return this.server.getOfflinePlayer(uuid);
    }

    public Optional<Player> getPlayer(User user) {
        return this.getPlayer(user.getUUID());
    }

    public Optional<Player> getPlayer(String name) {
        return Optional.ofNullable(this.server.getPlayer(name));
    }

    public Optional<Player> getPlayer(UUID uuid) {
        return Optional.ofNullable(this.server.getPlayer(uuid));
    }

}

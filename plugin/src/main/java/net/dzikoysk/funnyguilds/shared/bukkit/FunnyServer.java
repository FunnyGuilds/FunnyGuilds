package net.dzikoysk.funnyguilds.shared.bukkit;

import java.util.UUID;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import panda.std.Option;

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

    public Option<Player> getPlayer(User user) {
        return this.getPlayer(user.getUUID());
    }

    public Option<Player> getPlayer(String name) {
        return Option.of(this.server.getPlayer(name));
    }

    public Option<Player> getPlayer(UUID uuid) {
        return Option.of(this.server.getPlayer(uuid));
    }

}

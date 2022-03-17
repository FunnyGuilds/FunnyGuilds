package net.dzikoysk.funnyguilds.shared.bukkit;

import java.util.UUID;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import panda.std.Option;

public class FunnyServer {

    private final Server server;

    public FunnyServer(Server server) {
        this.server = server;
    }

    public Server getServer() {
        return server;
    }

    public Option<Player> getPlayer(String name) {
        return Option.of(server.getPlayer(name));
    }

    public Option<Player> getPlayer(UUID uuid) {
        return Option.of(server.getPlayer(uuid));
    }

}

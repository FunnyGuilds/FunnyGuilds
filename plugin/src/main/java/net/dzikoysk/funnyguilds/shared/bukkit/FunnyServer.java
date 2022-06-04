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
        return this.server;
    }

    public Option<Player> getPlayer(String name) {
        return Option.of(this.server.getPlayer(name));
    }

    public Option<Player> getPlayer(UUID uuid) {
        return Option.of(this.server.getPlayer(uuid));
    }

}

package net.dzikoysk.funnyguilds.concurrency.requests.prefix;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;

public class PrefixGlobalRemovePlayerRequest extends DefaultConcurrencyRequest {

    private final String player;
    private final FunnyGuilds plugin;

    public PrefixGlobalRemovePlayerRequest(String player, FunnyGuilds plugin) {
        this.player = player;
        this.plugin = plugin;
    }

    @Override
    public void execute() {
        plugin.getSystemManager().getIndividualPrefixManager().removePlayer(player);
    }

}

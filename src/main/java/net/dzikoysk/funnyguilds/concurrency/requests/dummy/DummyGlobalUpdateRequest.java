package net.dzikoysk.funnyguilds.concurrency.requests.dummy;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;

public class DummyGlobalUpdateRequest extends DefaultConcurrencyRequest {

    private final FunnyGuilds plugin;

    public DummyGlobalUpdateRequest(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute() {
        plugin.getSystemManager().getDummyManager().updatePlayers();
    }

}

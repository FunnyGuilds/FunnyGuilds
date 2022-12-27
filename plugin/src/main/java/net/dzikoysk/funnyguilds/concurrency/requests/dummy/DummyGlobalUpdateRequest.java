package net.dzikoysk.funnyguilds.concurrency.requests.dummy;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.feature.scoreboard.dummy.DummyManager;
import org.bukkit.Bukkit;

public class DummyGlobalUpdateRequest extends DefaultConcurrencyRequest {

    private final FunnyGuilds plugin;
    private final DummyManager dummyManager;

    public DummyGlobalUpdateRequest(FunnyGuilds plugin) {
        this.plugin = plugin;
        this.dummyManager = plugin.getDummyManager();
    }

    @Override
    public void execute() {
        Bukkit.getScheduler().runTask(this.plugin, this.dummyManager::updatePlayers);
    }

}

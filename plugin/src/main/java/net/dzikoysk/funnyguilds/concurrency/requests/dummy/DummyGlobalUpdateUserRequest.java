package net.dzikoysk.funnyguilds.concurrency.requests.dummy;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.feature.scoreboard.dummy.DummyManager;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;

public class DummyGlobalUpdateUserRequest extends DefaultConcurrencyRequest {

    private final FunnyGuilds plugin;
    private final DummyManager dummyManager;
    private final User user;

    public DummyGlobalUpdateUserRequest(FunnyGuilds plugin, User user) {
        this.plugin = plugin;
        this.dummyManager = plugin.getDummyManager();
        this.user = user;
    }

    @Override
    public void execute() {
        Bukkit.getScheduler().runTask(this.plugin, () -> this.dummyManager.updateScore(this.user));
    }

}

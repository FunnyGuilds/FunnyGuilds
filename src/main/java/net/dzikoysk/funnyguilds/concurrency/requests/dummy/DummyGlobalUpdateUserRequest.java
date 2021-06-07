package net.dzikoysk.funnyguilds.concurrency.requests.dummy;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;

public class DummyGlobalUpdateUserRequest extends DefaultConcurrencyRequest {

    private final User user;
    private final FunnyGuilds plugin;

    public DummyGlobalUpdateUserRequest(User user, FunnyGuilds plugin) {
        this.user = user;
        this.plugin = plugin;
    }

    @Override
    public void execute() {
        plugin.getSystemManager().getDummyManager().updateScore(user);
    }

}

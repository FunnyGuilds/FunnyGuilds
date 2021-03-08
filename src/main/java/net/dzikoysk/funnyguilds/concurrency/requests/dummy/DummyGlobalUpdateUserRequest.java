package net.dzikoysk.funnyguilds.concurrency.requests.dummy;

import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.element.DummyManager;

public class DummyGlobalUpdateUserRequest extends DefaultConcurrencyRequest {

    private final User user;

    public DummyGlobalUpdateUserRequest(User user) {
        this.user = user;
    }

    @Override
    public void execute() {
        DummyManager.updateScore(user);
    }

}

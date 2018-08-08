package net.dzikoysk.funnyguilds.concurrency.requests.dummy;

import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;

public class DummyUpdateUserRequest extends DefaultConcurrencyRequest {

    private final User user;

    public DummyUpdateUserRequest(User user) {
        this.user = user;
    }

    @Override
    public void execute() {
        user.getCache().getDummy().updateScore(user);
    }

}

package net.dzikoysk.funnyguilds.concurrency.requests.dummy;

import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.user.User;

public class DummyUpdateUserRequest extends DefaultConcurrencyRequest {

    private final User user;

    public DummyUpdateUserRequest(User user) {
        this.user = user;
    }

    @Override
    public void execute() {
        this.user.getCache().getDummy().updateScore(this.user);
    }

}

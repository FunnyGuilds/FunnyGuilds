package net.dzikoysk.funnyguilds.concurrency.requests.dummy;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.element.Dummy;

public class DummyUpdateUserRequest extends DefaultConcurrencyRequest {

    private final User user;

    public DummyUpdateUserRequest(User user) {
        this.user = user;
    }

    @Override
    public void execute() throws Exception {
        Dummy dummy = user.getDummy();
        dummy.updateScore(user);
    }

}

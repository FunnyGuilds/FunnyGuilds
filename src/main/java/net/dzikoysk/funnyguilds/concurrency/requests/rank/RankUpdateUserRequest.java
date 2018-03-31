package net.dzikoysk.funnyguilds.concurrency.requests.rank;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.RankManager;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;

public class RankUpdateUserRequest extends DefaultConcurrencyRequest {

    private final User user;

    public RankUpdateUserRequest(User user) {
        this.user = user;
    }

    @Override
    public void execute() throws Exception {
        RankManager.getInstance().update(user);
    }

}

package net.dzikoysk.funnyguilds.concurrency.requests.rank;

import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.rank.RankManager;
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

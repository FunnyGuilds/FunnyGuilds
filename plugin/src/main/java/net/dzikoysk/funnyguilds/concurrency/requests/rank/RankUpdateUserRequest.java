package net.dzikoysk.funnyguilds.concurrency.requests.rank;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.user.User;

public class RankUpdateUserRequest extends DefaultConcurrencyRequest {

    private final User user;

    public RankUpdateUserRequest(User user) {
        this.user = user;
    }

    @Override
    public void execute() throws Exception {
        FunnyGuilds.getInstance().getRankManager().update(user);
    }

}

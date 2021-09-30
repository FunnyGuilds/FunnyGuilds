package net.dzikoysk.funnyguilds.concurrency.requests.rank;

import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.rank.RankManager;
import net.dzikoysk.funnyguilds.user.UserManager;

public class RankUpdateUserRequest extends DefaultConcurrencyRequest {

    private final RankManager rankManager;
    private final UserManager userManager;

    public RankUpdateUserRequest(RankManager rankManager, UserManager userManager) {
        this.rankManager = rankManager;
        this.userManager = userManager;
    }

    @Override
    public void execute() throws Exception {
        this.rankManager.recalculateUsersRank(this.userManager);
    }

}

package net.dzikoysk.funnyguilds.rank;

import net.dzikoysk.funnyguilds.user.UserManager;

public class RankRecalculationTask implements Runnable {

    private final RankManager rankManager;
    private final UserManager userManager;

    public RankRecalculationTask(RankManager rankManager, UserManager userManager) {
        this.rankManager = rankManager;
        this.userManager = userManager;
    }

    @Override
    public void run() {
        this.rankManager.recalculateUsersRank(this.userManager);
        this.rankManager.recalculateGuildsRank();
    }

}

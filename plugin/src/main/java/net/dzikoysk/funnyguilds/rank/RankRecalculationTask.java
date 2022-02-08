package net.dzikoysk.funnyguilds.rank;

import net.dzikoysk.funnyguilds.FunnyGuilds;

public class RankRecalculationTask implements Runnable {

    private final RankManager rankManager;

    public RankRecalculationTask(FunnyGuilds plugin) {
        this.rankManager = plugin.getRankManager();
    }

    @Override
    public void run() {
        this.recalculateUsersRank(this.rankManager);
        this.recalculateGuildsRank(this.rankManager);
    }

    private void recalculateUsersRank(RankManager rankManager) {
        rankManager.userTopMap.forEach((key, top) -> {
            top.recalculate();
        });
    }

    public void recalculateGuildsRank(RankManager rankManager) {
        rankManager.guildTopMap.forEach((key, top) -> {
            top.recalculate();
        });
    }
}

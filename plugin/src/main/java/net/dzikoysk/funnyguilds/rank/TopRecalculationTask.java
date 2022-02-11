package net.dzikoysk.funnyguilds.rank;

import net.dzikoysk.funnyguilds.FunnyGuilds;

public class TopRecalculationTask implements Runnable {

    private final RankManager rankManager;

    public TopRecalculationTask(FunnyGuilds plugin) {
        this.rankManager = plugin.getRankManager();
    }

    @Override
    public void run() {
        this.recalculateUsersTop(this.rankManager);
        this.recalculateGuildsTop(this.rankManager);
    }

    private void recalculateUsersTop(RankManager rankManager) {
        rankManager.userTopMap.forEach((key, top) -> {
            top.recalculate(key);
        });
    }

    private void recalculateGuildsTop(RankManager rankManager) {
        rankManager.guildTopMap.forEach((key, top) -> {
            top.recalculate(key);
        });
    }
}

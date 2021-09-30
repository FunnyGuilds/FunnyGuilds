package net.dzikoysk.funnyguilds.concurrency.requests.rank;

import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.rank.RankManager;

public class RankUpdateGuildRequest extends DefaultConcurrencyRequest {

    private final RankManager rankManager;

    public RankUpdateGuildRequest(RankManager rankManager) {
        this.rankManager = rankManager;
    }

    @Override
    public void execute() throws Exception {
        this.rankManager.recalculateGuildsRank();
    }

}

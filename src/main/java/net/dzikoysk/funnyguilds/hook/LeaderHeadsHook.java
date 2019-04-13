package net.dzikoysk.funnyguilds.hook;

import com.google.common.collect.Maps;
import me.robin.leaderheads.datacollectors.DataCollector;
import me.robin.leaderheads.objects.BoardType;
import net.dzikoysk.funnyguilds.basic.rank.RankManager;
import net.dzikoysk.funnyguilds.basic.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

public class LeaderHeadsHook {

    public static void initLeaderHeadsHook() {
        new TopRankCollector();
    }

    public static class TopRankCollector extends DataCollector {
        public TopRankCollector() {
            super(
                    "funnyguilds-top-rank",
                    "FunnyGuilds",
                    BoardType.DEFAULT,
                    "Top rankingu",
                    "/toprank",
                    Collections.emptyList(),
                    true,
                    String.class
            );
        }

        @Override
        public List<Entry<?, Double>> requestAll() {
            List<Entry<?, Double>> topUsers = new ArrayList<>();

            for (int i = 1; i <= 10; i++) {
                User user = RankManager.getInstance().getUser(i);
                topUsers.add(Maps.immutableEntry(user.getName(), ((double) user.getRank().getPoints())));
            }

            return topUsers;
        }
    }

}

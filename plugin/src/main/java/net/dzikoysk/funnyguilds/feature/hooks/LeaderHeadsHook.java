package net.dzikoysk.funnyguilds.feature.hooks;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import me.robin.leaderheads.datacollectors.DataCollector;
import me.robin.leaderheads.objects.BoardType;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.user.User;

public class LeaderHeadsHook extends AbstractPluginHook {

    LeaderHeadsHook(String name) {
        super(name);
    }

    @Override
    public void init() {
        new TopRankCollector();
        super.init();
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
                User user = FunnyGuilds.getInstance().getRankManager().getUser(i);
                topUsers.add(Maps.immutableEntry(user.getName(), ((double) user.getRank().getPoints())));
            }

            return topUsers;
        }
    }

}

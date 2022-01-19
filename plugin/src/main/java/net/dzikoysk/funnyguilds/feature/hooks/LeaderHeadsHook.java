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

    private final FunnyGuilds plugin;

    LeaderHeadsHook(String name, FunnyGuilds plugin) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public void earlyInit() {
    }

    @Override
    public void init() {
        new TopRankCollector(plugin);
    }

    public static class TopRankCollector extends DataCollector {

        private final FunnyGuilds plugin;

        public TopRankCollector(FunnyGuilds plugin) {
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
            this.plugin = plugin;
        }

        @Override
        public List<Entry<?, Double>> requestAll() {
            List<Entry<?, Double>> topUsers = new ArrayList<>();

            for (int i = 1; i <= 10; i++) {
                User user = this.plugin.getRankManager().getUser(i);
                topUsers.add(Maps.immutableEntry(user.getName(), ((double) user.getRank().getPoints())));
            }

            return topUsers;
        }

    }

}

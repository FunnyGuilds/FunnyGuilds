package net.dzikoysk.funnyguilds.user.top;

import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.function.BiFunction;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.rank.TopComparator;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import net.dzikoysk.funnyguilds.user.UserRank;
import panda.std.stream.PandaStream;

public class UserRecalculation implements BiFunction<String, TopComparator<UserRank>, NavigableSet<UserRank>> {

    private final PluginConfiguration pluginConfiguration;
    private final UserManager userManager;

    public UserRecalculation(PluginConfiguration pluginConfiguration, UserManager userManager) {
        this.pluginConfiguration = pluginConfiguration;
        this.userManager = userManager;
    }

    @Override
    public NavigableSet<UserRank> apply(String id, TopComparator<UserRank> topComparator) {
        NavigableSet<UserRank> usersRank = new TreeSet<>(topComparator);

        PandaStream.of(userManager.getUsers())
                .filterNot(user -> this.pluginConfiguration.skipPrivilegedPlayersInRankPositions && user.hasPermission("funnyguilds.ranking.exempt"))
                .map(User::getRank)
                .forEach(usersRank::add);

        int position = 0;
        for (UserRank userRank : usersRank) {
            userRank.setPosition(id, ++position);
        }

        return usersRank;
    }

}
package net.dzikoysk.funnyguilds.user.top;

import java.util.Collections;
import java.util.Comparator;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.shared.bukkit.PermissionUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import net.dzikoysk.funnyguilds.user.UserRank;

public class UserRecalculation implements Function<Comparator<UserRank>, NavigableSet<UserRank>> {

    private final PluginConfiguration pluginConfiguration;
    private final UserManager userManager;

    public UserRecalculation(PluginConfiguration pluginConfiguration, UserManager userManager) {
        this.pluginConfiguration = pluginConfiguration;
        this.userManager = userManager;
    }

    @Override
    public NavigableSet<UserRank> apply(Comparator<UserRank> userRankComparator) {
        NavigableSet<UserRank> usersRank = new TreeSet<>(Collections.reverseOrder(userRankComparator));

        for (User user : userManager.getUsers()) {
            UserRank userRank = user.getRank();

            if (this.pluginConfiguration.skipPrivilegedPlayersInRankPositions &&
                    PermissionUtils.isPrivileged(user, "funnyguilds.ranking.exempt")) {
                continue;
            }

            usersRank.add(userRank);
        }

        int position = 0;

        for (UserRank userRank : usersRank) {
            userRank.setPosition(++position);
        }

        return usersRank;
    }

}
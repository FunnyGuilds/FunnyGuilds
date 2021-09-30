package net.dzikoysk.funnyguilds.rank;

import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildRank;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.PermissionUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import net.dzikoysk.funnyguilds.user.UserRank;

import java.util.Collections;
import java.util.NavigableSet;
import java.util.TreeSet;

public class RankRecalculationTask implements Runnable {

    private final PluginConfiguration pluginConfiguration;

    private final RankManager rankManager;
    private final UserManager userManager;

    public RankRecalculationTask(PluginConfiguration pluginConfiguration, RankManager rankManager, UserManager userManager) {
        this.pluginConfiguration = pluginConfiguration;
        this.rankManager = rankManager;
        this.userManager = userManager;
    }

    @Override
    public void run() {
        this.recalculateUsersRank(this.rankManager);
        this.recalculateGuildsRank(this.rankManager);
    }

    private void recalculateUsersRank(RankManager rankManager) {
        NavigableSet<UserRank> usersRank = new TreeSet<>(Collections.reverseOrder());

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

        rankManager.usersRank = usersRank;
    }

    public void recalculateGuildsRank(RankManager rankManager) {
        NavigableSet<GuildRank> guildsRank = new TreeSet<>(Collections.reverseOrder());

        for (Guild guild : GuildUtils.getGuilds()) {
            GuildRank guildRank = guild.getRank();

            if (!guild.isRanked()) {
                continue;
            }

            guildsRank.add(guildRank);
        }

        int position = 0;

        for (GuildRank guildRank : guildsRank) {
            guildRank.setPosition(++position);
        }

        rankManager.guildsRank = guildsRank;
    }

}

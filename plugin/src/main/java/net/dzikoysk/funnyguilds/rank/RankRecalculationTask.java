package net.dzikoysk.funnyguilds.rank;

import java.util.Collections;
import java.util.NavigableSet;
import java.util.TreeSet;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildRank;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.PermissionUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserRank;

public class RankRecalculationTask implements Runnable {

    private final FunnyGuilds plugin;

    public RankRecalculationTask(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        RankManager rankManager = plugin.getRankManager();

        this.recalculateUsersRank(rankManager);
        this.recalculateGuildsRank(rankManager);
    }

    private void recalculateUsersRank(RankManager manager) {
        NavigableSet<UserRank> usersRank = new TreeSet<>(Collections.reverseOrder());

        for (User user : plugin.getUserManager().getUsers()) {
            UserRank userRank = user.getRank();

            if (plugin.getPluginConfiguration().skipPrivilegedPlayersInRankPositions &&
                    PermissionUtils.isPrivileged(user, "funnyguilds.ranking.exempt")) {
                continue;
            }

            usersRank.add(userRank);
        }

        int position = 0;

        for (UserRank userRank : usersRank) {
            userRank.setPosition(++position);
        }

        manager.usersRank = usersRank;
    }

    private void recalculateGuildsRank(RankManager manager) {
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

        manager.guildsRank = guildsRank;
    }

}

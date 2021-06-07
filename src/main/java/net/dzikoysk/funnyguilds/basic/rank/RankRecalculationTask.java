package net.dzikoysk.funnyguilds.basic.rank;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.util.commons.bukkit.PermissionUtils;

import java.util.Collections;
import java.util.NavigableSet;
import java.util.TreeSet;

public class RankRecalculationTask implements Runnable {

    private final FunnyGuilds plugin;

    public RankRecalculationTask(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        RankManager manager = RankManager.getInstance();

        this.recalculateUsersRank(manager);
        this.recalculateGuildsRank(manager);
    }

    private void recalculateUsersRank(RankManager manager) {
        NavigableSet<Rank> usersRank = new TreeSet<>(Collections.reverseOrder());

        for (User user : plugin.getUserManager().getUsers()) {
            Rank userRank = user.getRank();

            if (FunnyGuilds.getInstance().getPluginConfiguration().skipPrivilegedPlayersInRankPositions &&
                    PermissionUtils.isPrivileged(user, "funnyguilds.ranking.exempt")) {
                continue;
            }

            usersRank.add(userRank);
        }

        int position = 0;

        for (Rank userRank : usersRank) {
            userRank.setPosition(++position);
        }

        manager.usersRank = usersRank;
    }

    private void recalculateGuildsRank(RankManager manager) {
        NavigableSet<Rank> guildsRank = new TreeSet<>(Collections.reverseOrder());

        for (Guild guild : GuildUtils.getGuilds()) {
            Rank guildRank = guild.getRank();

            if (guild.getMembers().size() < FunnyGuilds.getInstance().getPluginConfiguration().minMembersToInclude) {
                continue;
            }

            guildsRank.add(guildRank);
        }

        int position = 0;

        for (Rank guildRank : guildsRank) {
            guildRank.setPosition(++position);
        }

        manager.guildsRank = guildsRank;
    }
}

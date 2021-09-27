package net.dzikoysk.funnyguilds.rank;

import com.google.common.collect.Iterables;
import java.util.Collections;
import java.util.NavigableSet;
import java.util.TreeSet;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildRank;
import net.dzikoysk.funnyguilds.shared.bukkit.PermissionUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserRank;

public class RankManager {

    protected NavigableSet<UserRank> usersRank = new TreeSet<>(Collections.reverseOrder());
    protected NavigableSet<GuildRank> guildsRank = new TreeSet<>(Collections.reverseOrder());

    @Deprecated
    private static RankManager INSTANCE;

    public RankManager() {
        INSTANCE = this;
    }

    public void update(User user) {
        if (user.getUUID().version() == 2) {
            return;
        }

        if (FunnyGuilds.getInstance().getPluginConfiguration().skipPrivilegedPlayersInRankPositions &&
                PermissionUtils.isPrivileged(user, "funnyguilds.ranking.exempt")) {
            return;
        }

        this.usersRank.add(user.getRank());
    }

    public void update(Guild guild) {
        if (!guild.isRanked()) {
            return;
        }

        this.guildsRank.add(guild.getRank());
    }

    public User getUser(int place) {
        if (place - 1 < this.usersRank.size()) {
            return Iterables.get(this.usersRank, place - 1).getUser();
        }

        return null;
    }

    public Guild getGuild(int place) {
        if (place - 1 < this.guildsRank.size()) {
            return Iterables.get(this.guildsRank, place - 1).getGuild();
        }

        return null;
    }

    public int countUsers() {
        return this.usersRank.size();
    }

    public int countGuilds() {
        return this.guildsRank.size();
    }

    /**
     * Gets the rank manager.
     *
     * @return the rank manager
     * @deprecated for removal in the future, in favour of {@link FunnyGuilds#getRankManager()}
     */
    @Deprecated
    public static RankManager getInstance() {
        return INSTANCE;
    }

}
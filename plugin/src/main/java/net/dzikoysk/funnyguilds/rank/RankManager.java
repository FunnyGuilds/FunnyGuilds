package net.dzikoysk.funnyguilds.rank;

import com.google.common.collect.Iterables;
import net.dzikoysk.funnyguilds.FunnyGuilds;
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

public class RankManager {

    private final PluginConfiguration pluginConfiguration;

    protected NavigableSet<UserRank> usersRank = new TreeSet<>(Collections.reverseOrder());
    protected NavigableSet<GuildRank> guildsRank = new TreeSet<>(Collections.reverseOrder());

    @Deprecated
    private static RankManager INSTANCE;

    public RankManager(PluginConfiguration pluginConfiguration) {
        this.pluginConfiguration = pluginConfiguration;

        INSTANCE = this;
    }

    public void recalculateUsersRank(UserManager userManager) {
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

        this.usersRank = usersRank;
    }

    public void recalculateGuildsRank() {
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

        this.guildsRank = guildsRank;
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
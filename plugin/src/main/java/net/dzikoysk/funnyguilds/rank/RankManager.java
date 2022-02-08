package net.dzikoysk.funnyguilds.rank;

import java.util.HashMap;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.top.GuildComparator;
import net.dzikoysk.funnyguilds.guild.top.GuildRecalculation;
import net.dzikoysk.funnyguilds.guild.top.GuildTop;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import net.dzikoysk.funnyguilds.user.top.UserComparator;
import net.dzikoysk.funnyguilds.user.top.UserRecalculation;
import net.dzikoysk.funnyguilds.user.top.UserTop;
import panda.std.Option;

public class RankManager {

    private final PluginConfiguration pluginConfiguration;

    protected HashMap<String, UserTop> userTopMap = new HashMap<>();
    protected HashMap<String, GuildTop> guildTopMap = new HashMap<>();

    @Deprecated
    private static RankManager INSTANCE;

    public RankManager(PluginConfiguration pluginConfiguration, UserManager userManager, GuildManager guildManager) {
        this.pluginConfiguration = pluginConfiguration;

        UserRecalculation userRecalculation = new UserRecalculation(pluginConfiguration, userManager);
        GuildRecalculation guildRecalculation = new GuildRecalculation(guildManager);

        userTopMap.put("points", new UserTop(UserComparator.POINTS_COMPARATOR, userRecalculation));
        guildTopMap.put("avg_points", new GuildTop(GuildComparator.AVG_POINTS_COMPARATOR, guildRecalculation));

        INSTANCE = this;
    }

    public Option<UserTop> getUserTop(String id) {
        return Option.of(this.userTopMap.get(id))
                .orElse(this.userTopMap.get("points"));
    }

    public Option<GuildTop> getGuildTop(String id) {
        return Option.of(this.guildTopMap.get(id))
                .orElse(this.guildTopMap.get("avg_points"));
    }

    public User getUser(int place) {
        return getUserTop("points")
                .map(top -> top.getUser(place))
                .getOrNull();
    }

    public Guild getGuild(int place) {
        return getGuildTop("avg_points")
                .map(top -> top.getGuild(place))
                .getOrNull();
    }

    public boolean isRankedGuild(Guild guild) {
        return guild.getMembers().size() >= pluginConfiguration.minMembersToInclude;
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
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

    private final UserManager userManager;
    private final GuildManager guildManager;

    protected HashMap<String, UserTop> userTopMap = new HashMap<>();
    protected HashMap<String, GuildTop> guildTopMap = new HashMap<>();

    @Deprecated
    private static RankManager INSTANCE;

    public RankManager(PluginConfiguration pluginConfiguration, UserManager userManager, GuildManager guildManager) {
        this.pluginConfiguration = pluginConfiguration;

        this.userManager = userManager;
        this.guildManager = guildManager;

        INSTANCE = this;
    }

    public void addDefaultTops() {
        UserRecalculation userRecalculation = new UserRecalculation(pluginConfiguration, userManager);
        GuildRecalculation guildRecalculation = new GuildRecalculation(guildManager);

        this.addUserTop("points", new UserTop(UserComparator.POINTS_COMPARATOR, userRecalculation));
        this.addUserTop("kills", new UserTop(UserComparator.KILLS_COMPARATOR, userRecalculation));
        this.addUserTop("deaths", new UserTop(UserComparator.DEATHS_COMPARATOR, userRecalculation));
        this.addUserTop("kdr", new UserTop(UserComparator.KDR_COMPARATOR, userRecalculation));
        this.addUserTop("assists", new UserTop(UserComparator.ASSISTS_COMPARATOR, userRecalculation));
        this.addUserTop("logouts", new UserTop(UserComparator.LOGOUTS_COMPARATOR, userRecalculation));

        this.addGuildTop("points", new GuildTop(GuildComparator.POINTS_COMPARATOR, guildRecalculation));
        this.addGuildTop("kills", new GuildTop(GuildComparator.KILLS_COMPARATOR, guildRecalculation));
        this.addGuildTop("deaths", new GuildTop(GuildComparator.DEATHS_COMPARATOR, guildRecalculation));
        this.addGuildTop("kdr", new GuildTop(GuildComparator.KDR_COMPARATOR, guildRecalculation));
        this.addGuildTop("assists", new GuildTop(GuildComparator.ASSISTS_COMPARATOR, guildRecalculation));
        this.addGuildTop("logouts", new GuildTop(GuildComparator.LOGOUTS_COMPARATOR, guildRecalculation));

        this.addGuildTop("avg_points", new GuildTop(GuildComparator.AVG_POINTS_COMPARATOR, guildRecalculation));
        this.addGuildTop("avg_kills", new GuildTop(GuildComparator.AVG_KILLS_COMPARATOR, guildRecalculation));
        this.addGuildTop("avg_deaths", new GuildTop(GuildComparator.AVG_DEATHS_COMPARATOR, guildRecalculation));
        this.addGuildTop("avg_kdr", new GuildTop(GuildComparator.AVG_KDR_COMPARATOR, guildRecalculation));
        this.addGuildTop("avg_assists", new GuildTop(GuildComparator.AVG_ASSISTS_COMPARATOR, guildRecalculation));
        this.addGuildTop("avg_logouts", new GuildTop(GuildComparator.AVG_LOGOUTS_COMPARATOR, guildRecalculation));
    }

    public Option<UserTop> getUserTop(String id) {
        return Option.of(this.userTopMap.get(id))
                .orElse(this.userTopMap.get("points"));
    }

    private void addUserTop(String id, UserTop userTop) {
        this.userTopMap.put(id, userTop);
    }

    public Option<GuildTop> getGuildTop(String id) {
        return Option.of(this.guildTopMap.get(id))
                .orElse(this.guildTopMap.get("avg_points"));
    }

    private void addGuildTop(String id, GuildTop guildTop) {
        this.guildTopMap.put(id, guildTop);
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
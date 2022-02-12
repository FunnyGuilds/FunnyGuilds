package net.dzikoysk.funnyguilds.rank;

import java.util.HashMap;
import java.util.Map;
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
import panda.std.stream.PandaStream;

public class RankManager {

    public static final String USER_POINTS_TOP = "points";
    public static final String USER_KILLS_TOP = "kills";
    public static final String USER_DEATHS_TOP = "deaths";
    public static final String USER_KDR_TOP = "kdr";
    public static final String USER_ASSISTS_TOP = "assists";
    public static final String USER_LOGOUTS_TOP = "logouts";

    public static final String GUILD_POINTS_TOP = "points";
    public static final String GUILD_KILLS_TOP = "kills";
    public static final String GUILD_DEATHS_TOP = "deaths";
    public static final String GUILD_KDR_TOP = "kdr";
    public static final String GUILD_ASSISTS_TOP = "assists";
    public static final String GUILD_LOGOUTS_TOP = "logouts";

    public static final String GUILD_AVG_POINTS_TOP = "avg_points";
    public static final String GUILD_AVG_KILLS_TOP = "avg_kills";
    public static final String GUILD_AVG_DEATHS_TOP = "avg_deaths";
    public static final String GUILD_AVG_KDR_TOP = "avg_kdr";
    public static final String GUILD_AVG_ASSISTS_TOP = "avg_assists";
    public static final String GUILD_AVG_LOGOUTS_TOP = "avg_logouts";

    private final PluginConfiguration pluginConfiguration;

    protected final Map<String, UserTop> userTopMap = new HashMap<>();
    protected final Map<String, GuildTop> guildTopMap = new HashMap<>();

    @Deprecated
    private static RankManager INSTANCE;

    public RankManager(PluginConfiguration pluginConfiguration) {
        this.pluginConfiguration = pluginConfiguration;

        INSTANCE = this;
    }

    public Map<String, UserTop> getUserTopMap() {
        return userTopMap;
    }

    public Map<String, GuildTop> getGuildTopMap() {
        return guildTopMap;
    }

    public Option<UserTop> getUserTop(String id) {
        return Option.of(this.userTopMap.get(id.toLowerCase()));
    }

    public void addUserTop(String id, UserTop userTop) {
        this.userTopMap.put(id.toLowerCase(), userTop);
    }

    private void register(String id, UserTop userTop) {
        if (PandaStream.of(this.pluginConfiguration.top.enabledUserTops)
                .find(enabledTop -> enabledTop.equalsIgnoreCase(id))
                .isEmpty()) {
            return;
        }
        this.userTopMap.put(id.toLowerCase(), userTop);
    }

    public Option<GuildTop> getGuildTop(String id) {
        return Option.of(this.guildTopMap.get(id.toLowerCase()));
    }

    public void addGuildTop(String id, GuildTop guildTop) {
        this.guildTopMap.put(id.toLowerCase(), guildTop);
    }

    private void register(String id, GuildTop guildTop) {
        if (PandaStream.of(this.pluginConfiguration.top.enabledGuildTops)
                .find(enabledTop -> enabledTop.equalsIgnoreCase(id))
                .isEmpty()) {
            return;
        }
        this.guildTopMap.put(id, guildTop);
    }

    public Option<User> getUser(int place) {
        return getUserTop(USER_POINTS_TOP)
                .flatMap(top -> top.getUser(place));
    }

    public Option<Guild> getGuild(int place) {
        return getGuildTop(GUILD_AVG_POINTS_TOP)
                .flatMap(top -> top.getGuild(place));
    }

    public boolean isRankedGuild(Guild guild) {
        return guild.getMembers().size() >= pluginConfiguration.minMembersToInclude;
    }

    public void registerDefaultUserTops(UserManager userManager) {
        UserRecalculation userRecalculation = new UserRecalculation(this.pluginConfiguration, userManager);

        register(USER_POINTS_TOP, new UserTop(UserComparator.POINTS_COMPARATOR, userRecalculation));
        register(USER_KILLS_TOP, new UserTop(UserComparator.KILLS_COMPARATOR, userRecalculation));
        register(USER_DEATHS_TOP, new UserTop(UserComparator.DEATHS_COMPARATOR, userRecalculation));
        register(USER_KDR_TOP, new UserTop(UserComparator.KDR_COMPARATOR, userRecalculation));
        register(USER_ASSISTS_TOP, new UserTop(UserComparator.ASSISTS_COMPARATOR, userRecalculation));
        register(USER_LOGOUTS_TOP, new UserTop(UserComparator.LOGOUTS_COMPARATOR, userRecalculation));
    }

    public void registerDefaultGuildTops(GuildManager guildManager) {
        GuildRecalculation guildRecalculation = new GuildRecalculation(guildManager);

        register(GUILD_POINTS_TOP, new GuildTop(GuildComparator.POINTS_COMPARATOR, guildRecalculation));
        register(GUILD_KILLS_TOP, new GuildTop(GuildComparator.KILLS_COMPARATOR, guildRecalculation));
        register(GUILD_DEATHS_TOP, new GuildTop(GuildComparator.DEATHS_COMPARATOR, guildRecalculation));
        register(GUILD_KDR_TOP, new GuildTop(GuildComparator.KDR_COMPARATOR, guildRecalculation));
        register(GUILD_ASSISTS_TOP, new GuildTop(GuildComparator.ASSISTS_COMPARATOR, guildRecalculation));
        register(GUILD_LOGOUTS_TOP, new GuildTop(GuildComparator.LOGOUTS_COMPARATOR, guildRecalculation));

        register(GUILD_AVG_POINTS_TOP, new GuildTop(GuildComparator.AVG_POINTS_COMPARATOR, guildRecalculation));
        register(GUILD_AVG_KILLS_TOP, new GuildTop(GuildComparator.AVG_KILLS_COMPARATOR, guildRecalculation));
        register(GUILD_AVG_DEATHS_TOP, new GuildTop(GuildComparator.AVG_DEATHS_COMPARATOR, guildRecalculation));
        register(GUILD_AVG_KDR_TOP, new GuildTop(GuildComparator.AVG_KDR_COMPARATOR, guildRecalculation));
        register(GUILD_AVG_ASSISTS_TOP, new GuildTop(GuildComparator.AVG_ASSISTS_COMPARATOR, guildRecalculation));
        register(GUILD_AVG_LOGOUTS_TOP, new GuildTop(GuildComparator.AVG_LOGOUTS_COMPARATOR, guildRecalculation));
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
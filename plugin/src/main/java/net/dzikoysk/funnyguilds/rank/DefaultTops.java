package net.dzikoysk.funnyguilds.rank;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.top.GuildComparator;
import net.dzikoysk.funnyguilds.guild.top.GuildRecalculation;
import net.dzikoysk.funnyguilds.guild.top.GuildTop;
import net.dzikoysk.funnyguilds.user.UserManager;
import net.dzikoysk.funnyguilds.user.top.UserComparator;
import net.dzikoysk.funnyguilds.user.top.UserRecalculation;
import net.dzikoysk.funnyguilds.user.top.UserTop;

public final class DefaultTops {

    public static final String USER_POINTS_TOP = "points";
    public static final String USER_KILLS_TOP = "kills";
    public static final String USER_DEATHS_TOP = "deaths";
    public static final String USER_KDR_TOP = "kdr";
    public static final String USER_KDA_TOP = "kda";
    public static final String USER_ASSISTS_TOP = "assists";
    public static final String USER_LOGOUTS_TOP = "logouts";

    public static final String GUILD_POINTS_TOP = "points";
    public static final String GUILD_KILLS_TOP = "kills";
    public static final String GUILD_DEATHS_TOP = "deaths";
    public static final String GUILD_KDR_TOP = "kdr";
    public static final String GUILD_KDA_TOP = "kda";
    public static final String GUILD_ASSISTS_TOP = "assists";
    public static final String GUILD_LOGOUTS_TOP = "logouts";

    public static final String GUILD_AVG_POINTS_TOP = "avg_points";
    public static final String GUILD_AVG_KILLS_TOP = "avg_kills";
    public static final String GUILD_AVG_DEATHS_TOP = "avg_deaths";
    public static final String GUILD_AVG_KDR_TOP = "avg_kdr";
    public static final String GUILD_AVG_KDA_TOP = "avg_kda";
    public static final String GUILD_AVG_ASSISTS_TOP = "avg_assists";
    public static final String GUILD_AVG_LOGOUTS_TOP = "avg_logouts";

    private DefaultTops() {
    }

    public static Map<String, UserTop> defaultUserTops(PluginConfiguration pluginConfiguration, UserManager userManager) {
        UserRecalculation recalculation = new UserRecalculation(pluginConfiguration, userManager);
        return ImmutableMap.<String, UserTop>builder()
                .put(USER_POINTS_TOP, new UserTop(UserComparator.POINTS_COMPARATOR, recalculation))
                .put(USER_KILLS_TOP, new UserTop(UserComparator.KILLS_COMPARATOR, recalculation))
                .put(USER_DEATHS_TOP, new UserTop(UserComparator.DEATHS_COMPARATOR, recalculation))
                .put(USER_KDR_TOP, new UserTop(UserComparator.KDR_COMPARATOR, recalculation))
                .put(USER_KDA_TOP, new UserTop(UserComparator.KDA_COMPARATOR, recalculation))
                .put(USER_ASSISTS_TOP, new UserTop(UserComparator.ASSISTS_COMPARATOR, recalculation))
                .put(USER_LOGOUTS_TOP, new UserTop(UserComparator.LOGOUTS_COMPARATOR, recalculation))
                .build();
    }

    public static Map<String, GuildTop> defaultGuildTops(GuildManager guildManager) {
        GuildRecalculation recalculation = new GuildRecalculation(guildManager);
        return ImmutableMap.<String, GuildTop>builder()
                .put(GUILD_POINTS_TOP, new GuildTop(GuildComparator.POINTS_COMPARATOR, recalculation))
                .put(GUILD_KILLS_TOP, new GuildTop(GuildComparator.KILLS_COMPARATOR, recalculation))
                .put(GUILD_DEATHS_TOP, new GuildTop(GuildComparator.DEATHS_COMPARATOR, recalculation))
                .put(GUILD_KDR_TOP, new GuildTop(GuildComparator.KDR_COMPARATOR, recalculation))
                .put(GUILD_KDA_TOP, new GuildTop(GuildComparator.KDA_COMPARATOR, recalculation))
                .put(GUILD_ASSISTS_TOP, new GuildTop(GuildComparator.ASSISTS_COMPARATOR, recalculation))
                .put(GUILD_LOGOUTS_TOP, new GuildTop(GuildComparator.LOGOUTS_COMPARATOR, recalculation))
                .put(GUILD_AVG_POINTS_TOP, new GuildTop(GuildComparator.AVG_POINTS_COMPARATOR, recalculation))
                .put(GUILD_AVG_KILLS_TOP, new GuildTop(GuildComparator.AVG_KILLS_COMPARATOR, recalculation))
                .put(GUILD_AVG_DEATHS_TOP, new GuildTop(GuildComparator.AVG_DEATHS_COMPARATOR, recalculation))
                .put(GUILD_AVG_KDR_TOP, new GuildTop(GuildComparator.AVG_KDR_COMPARATOR, recalculation))
                .put(GUILD_AVG_KDA_TOP, new GuildTop(GuildComparator.AVG_KDA_COMPARATOR, recalculation))
                .put(GUILD_AVG_ASSISTS_TOP, new GuildTop(GuildComparator.AVG_ASSISTS_COMPARATOR, recalculation))
                .put(GUILD_AVG_LOGOUTS_TOP, new GuildTop(GuildComparator.AVG_LOGOUTS_COMPARATOR, recalculation))
                .build();
    }

}

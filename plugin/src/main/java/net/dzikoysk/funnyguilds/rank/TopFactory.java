package net.dzikoysk.funnyguilds.rank;

import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.top.GuildComparator;
import net.dzikoysk.funnyguilds.guild.top.GuildRecalculation;
import net.dzikoysk.funnyguilds.guild.top.GuildTop;
import net.dzikoysk.funnyguilds.user.UserManager;
import net.dzikoysk.funnyguilds.user.top.UserComparator;
import net.dzikoysk.funnyguilds.user.top.UserRecalculation;
import net.dzikoysk.funnyguilds.user.top.UserTop;
import panda.std.stream.PandaStream;

public class TopFactory {

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

    private final RankManager rankManager;

    public TopFactory(PluginConfiguration pluginConfiguration, RankManager rankManager) {
        this.pluginConfiguration = pluginConfiguration;
        this.rankManager = rankManager;
    }

    public void addDefaultTops(UserManager userManager, GuildManager guildManager) {
        UserRecalculation userRecalculation = new UserRecalculation(pluginConfiguration, userManager);
        GuildRecalculation guildRecalculation = new GuildRecalculation(guildManager);

        this.addUserTop(USER_POINTS_TOP, new UserTop(UserComparator.POINTS_COMPARATOR, userRecalculation));
        this.addUserTop(USER_KILLS_TOP, new UserTop(UserComparator.KILLS_COMPARATOR, userRecalculation));
        this.addUserTop(USER_DEATHS_TOP, new UserTop(UserComparator.DEATHS_COMPARATOR, userRecalculation));
        this.addUserTop(USER_KDR_TOP, new UserTop(UserComparator.KDR_COMPARATOR, userRecalculation));
        this.addUserTop(USER_ASSISTS_TOP, new UserTop(UserComparator.ASSISTS_COMPARATOR, userRecalculation));
        this.addUserTop(USER_LOGOUTS_TOP, new UserTop(UserComparator.LOGOUTS_COMPARATOR, userRecalculation));

        this.addGuildTop(GUILD_POINTS_TOP, new GuildTop(GuildComparator.POINTS_COMPARATOR, guildRecalculation));
        this.addGuildTop(GUILD_KILLS_TOP, new GuildTop(GuildComparator.KILLS_COMPARATOR, guildRecalculation));
        this.addGuildTop(GUILD_DEATHS_TOP, new GuildTop(GuildComparator.DEATHS_COMPARATOR, guildRecalculation));
        this.addGuildTop(GUILD_KDR_TOP, new GuildTop(GuildComparator.KDR_COMPARATOR, guildRecalculation));
        this.addGuildTop(GUILD_ASSISTS_TOP, new GuildTop(GuildComparator.ASSISTS_COMPARATOR, guildRecalculation));
        this.addGuildTop(GUILD_LOGOUTS_TOP, new GuildTop(GuildComparator.LOGOUTS_COMPARATOR, guildRecalculation));

        this.addGuildTop(GUILD_AVG_POINTS_TOP, new GuildTop(GuildComparator.AVG_POINTS_COMPARATOR, guildRecalculation));
        this.addGuildTop(GUILD_AVG_KILLS_TOP, new GuildTop(GuildComparator.AVG_KILLS_COMPARATOR, guildRecalculation));
        this.addGuildTop(GUILD_AVG_DEATHS_TOP, new GuildTop(GuildComparator.AVG_DEATHS_COMPARATOR, guildRecalculation));
        this.addGuildTop(GUILD_AVG_KDR_TOP, new GuildTop(GuildComparator.AVG_KDR_COMPARATOR, guildRecalculation));
        this.addGuildTop(GUILD_AVG_ASSISTS_TOP, new GuildTop(GuildComparator.AVG_ASSISTS_COMPARATOR, guildRecalculation));
        this.addGuildTop(GUILD_AVG_LOGOUTS_TOP, new GuildTop(GuildComparator.AVG_LOGOUTS_COMPARATOR, guildRecalculation));
    }

    private void addUserTop(String id, UserTop userTop) {
        if (PandaStream.of(this.pluginConfiguration.top.enabledUserTops)
                .find(enabledTop -> enabledTop.equalsIgnoreCase(id))
                .isEmpty()) {
            return;
        }
        this.rankManager.addUserTop(id.toLowerCase(), userTop);
    }

    private void addGuildTop(String id, GuildTop guildTop) {
        if (PandaStream.of(this.pluginConfiguration.top.enabledGuildTops)
                .find(enabledTop -> enabledTop.equalsIgnoreCase(id))
                .isEmpty()) {
            return;
        }
        this.rankManager.addGuildTop(id, guildTop);
    }

}

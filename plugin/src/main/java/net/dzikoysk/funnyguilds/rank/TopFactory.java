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

    private final PluginConfiguration pluginConfiguration;

    private final RankManager rankManager;

    public TopFactory(PluginConfiguration pluginConfiguration, RankManager rankManager) {
        this.pluginConfiguration = pluginConfiguration;
        this.rankManager = rankManager;
    }

    public void addDefaultTops(UserManager userManager, GuildManager guildManager) {
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

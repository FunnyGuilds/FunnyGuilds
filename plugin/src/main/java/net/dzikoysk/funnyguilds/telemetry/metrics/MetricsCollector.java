package net.dzikoysk.funnyguilds.telemetry.metrics;

import java.util.HashMap;
import java.util.Map;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.MultiLineChart;
import org.bstats.charts.SingleLineChart;

public class MetricsCollector implements Runnable {

    private final FunnyGuilds plugin;
    private final UserManager userManager;
    private final GuildManager guildManager;

    private Metrics bstats;

    public MetricsCollector(FunnyGuilds plugin) {
        this.plugin = plugin;
        this.userManager = plugin.getUserManager();
        this.guildManager = plugin.getGuildManager();

        try {
            this.bstats = new Metrics(plugin, 677);
        }
        catch (Exception exception) {
            this.bstats = null;
            FunnyGuilds.getPluginLogger().error("Could not initialize bstats", exception);
        }
    }

    public void start() {
        this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, this, 20L);
    }

    @Override
    public void run() {
        // bStats
        Metrics bstats = this.bstats;
        if (bstats != null) {
            bstats.addCustomChart(new SingleLineChart("users", this.userManager::countUsers));
            bstats.addCustomChart(new SingleLineChart("guilds", this.guildManager::countGuilds));
            bstats.addCustomChart(new MultiLineChart("users_and_guilds", () -> {
                Map<String, Integer> valueMap = new HashMap<>();

                valueMap.put("users", this.userManager.countUsers());
                valueMap.put("guilds", this.guildManager.countGuilds());

                return valueMap;
            }));
        }
    }

}

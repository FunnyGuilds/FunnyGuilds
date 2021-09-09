package net.dzikoysk.funnyguilds.telemetry.metrics;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.user.UserUtils;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.MultiLineChart;
import org.bstats.charts.SingleLineChart;

import java.util.HashMap;
import java.util.Map;

public class MetricsCollector implements Runnable {

    private final FunnyGuilds plugin;

    private MCStats mcstats;
    private Metrics bstats;

    public MetricsCollector(FunnyGuilds plugin) {
        this.plugin = plugin;

        try {
            mcstats = new MCStats(plugin);
        } catch (Exception ex) {
            this.mcstats = null;
            FunnyGuilds.getPluginLogger().error("Could not initialize mcstats", ex);
        }

        try {
            this.bstats = new Metrics(plugin, 677);
        } catch (Exception ex) {
            this.bstats = null;
            FunnyGuilds.getPluginLogger().error("Could not initialize bstats", ex);
        }
    }

    public void start() {
        this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, this, 20L);
    }

    @Override
    public void run() {
        // MCStats
        MCStats mcstats = this.mcstats;
        if (mcstats != null) {
            MCStats.Graph global = mcstats.createGraph("Guilds and Users");

            global.addPlotter(new MCStats.Plotter("Users") {
                @Override
                public int getValue() {
                    return plugin.getUserManager().usersCount();
                }
            });

            global.addPlotter(new MCStats.Plotter("Guilds") {
                @Override
                public int getValue() {
                    return GuildUtils.getGuilds().size();
                }
            });

            mcstats.start();
        }

        // bStats
        Metrics bstats = this.bstats;
        if (bstats != null) {
            bstats.addCustomChart(new SingleLineChart("users", () -> plugin.getUserManager().usersCount()));

            bstats.addCustomChart(new SingleLineChart("guilds", GuildUtils::guildsCount));

            bstats.addCustomChart(new MultiLineChart("users_and_guilds", () -> {
                Map<String, Integer> valueMap = new HashMap<>();

                valueMap.put("users", plugin.getUserManager().usersCount());
                valueMap.put("guilds", GuildUtils.guildsCount());

                return valueMap;
            }));
        }
    }
}

package net.dzikoysk.funnyguilds.util.metrics;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import org.bukkit.Bukkit;

public class MetricsCollector implements Runnable {

    private static Metrics metrics;

    public MetricsCollector() {
        try {
            metrics = new Metrics(FunnyGuilds.getInstance());
        } catch (Exception e) {
            if (FunnyGuilds.exception(e.getCause()))
                e.printStackTrace();
        }
    }

    public static Metrics getMetrics() {
        if (metrics == null)
            new MetricsCollector();
        return metrics;
    }

    public void start() {
        Bukkit.getScheduler().runTaskLaterAsynchronously(FunnyGuilds.getInstance(), this, 20L);
    }

    @Override
    public void run() {
        Metrics metrics = getMetrics();
        Metrics.Graph global = metrics.createGraph("Guilds and Users");
        global.addPlotter(new Metrics.Plotter("Guilds") {

            @Override
            public int getValue() {
                return GuildUtils.getGuilds().size();
            }
        });
        global.addPlotter(new Metrics.Plotter("Users") {

            @Override
            public int getValue() {
                return UserUtils.getUsers().size();
            }
        });
        metrics.start();
    }
}

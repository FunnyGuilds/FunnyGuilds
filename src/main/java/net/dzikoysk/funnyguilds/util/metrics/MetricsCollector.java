package net.dzikoysk.funnyguilds.util.metrics;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;

import java.util.HashMap;

public class MetricsCollector implements Runnable {

    private final FunnyGuilds plugin;

    private Metrics mcstats;
    private org.bstats.Metrics bstats;

    public MetricsCollector(FunnyGuilds plugin) {
        this.plugin = plugin;
        try {
            mcstats = new Metrics(plugin);
        } catch (Exception e) {
            this.mcstats = null;
            if (FunnyGuilds.exception(e.getCause())) {
                e.printStackTrace();
            }
        }
        try {
            this.bstats = new org.bstats.Metrics(plugin);
        } catch (Exception e) {
            this.bstats = null;
            if (FunnyGuilds.exception(e.getCause())) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, this, 20L);
    }

    @Override
    public void run() {
        // mcstats
        Metrics mcstats = this.mcstats;
        if (mcstats != null) {
            Metrics.Graph global = mcstats.createGraph("Guilds and Users");
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
            mcstats.start();
        }

        // bstats
        org.bstats.Metrics bstats = this.bstats;
        if (bstats != null) {
            bstats.addCustomChart(new org.bstats.Metrics.MultiLineChart("Guilds and Users") {
                @Override
                public HashMap<String, Integer> getValues(HashMap<String, Integer> hashMap) {
                    hashMap.put("Guilds", GuildUtils.getGuilds().size());
                    hashMap.put("Users", UserUtils.getUsers().size());
                    return hashMap;
                }
            });
        }
    }
}

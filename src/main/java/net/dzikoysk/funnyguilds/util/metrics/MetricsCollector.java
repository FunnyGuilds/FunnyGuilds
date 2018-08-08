package net.dzikoysk.funnyguilds.util.metrics;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.user.UserUtils;
import net.dzikoysk.funnyguilds.FunnyLogger;

import java.util.HashMap;

public class MetricsCollector implements Runnable {

    private final FunnyGuilds plugin;

    private MCStats mcstats;
    private BStats bstats;

    public MetricsCollector(FunnyGuilds plugin) {
        this.plugin = plugin;
        try {
            mcstats = new MCStats(plugin);
        } catch (Exception e) {
            this.mcstats = null;
            if (FunnyLogger.exception(e.getCause())) {
                e.printStackTrace();
            }
        }
        try {
            this.bstats = new BStats(plugin);
        } catch (Exception e) {
            this.bstats = null;
            if (FunnyLogger.exception(e.getCause())) {
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
        MCStats mcstats = this.mcstats;
        if (mcstats != null) {
            MCStats.Graph global = mcstats.createGraph("Guilds and Users");
            global.addPlotter(new MCStats.Plotter("Guilds") {
                @Override
                public int getValue() {
                    return GuildUtils.getGuilds().size();
                }
            });
            global.addPlotter(new MCStats.Plotter("Users") {
                @Override
                public int getValue() {
                    return UserUtils.usersSize();
                }
            });
            mcstats.start();
        }

        // bstats
        BStats bstats = this.bstats;
        if (bstats != null) {
            bstats.addCustomChart(new BStats.MultiLineChart("Guilds and Users") {
                @Override
                public HashMap<String, Integer> getValues(HashMap<String, Integer> hashMap) {
                    hashMap.put("Guilds", GuildUtils.getGuilds().size());
                    hashMap.put("Users", UserUtils.usersSize());
                    return hashMap;
                }
            });
        }
    }
}

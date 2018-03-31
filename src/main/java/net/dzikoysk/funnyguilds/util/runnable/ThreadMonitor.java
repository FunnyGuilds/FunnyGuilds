package net.dzikoysk.funnyguilds.util.runnable;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.FunnyLogger;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class ThreadMonitor implements Runnable {

    private final FunnyGuilds plugin;

    public ThreadMonitor(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    public void start() {
        this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, this, 0, 200);
    }

    @Override
    public void run() {
        ThreadMXBean tmxb = ManagementFactory.getThreadMXBean();
        long full = 0;
        
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            full += tmxb.getThreadCpuTime(t.getId());
        }
        
        FunnyLogger.info("================================");
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (tmxb.getThreadCpuTime(t.getId()) > 0) {
                long l = (tmxb.getThreadCpuTime(t.getId()) * 100L) / full;
                
                if (l > 0.0) {
                    FunnyLogger.info(t.getName() + ": " + l + "%");
                }
            }
        }
        
        FunnyLogger.info("================================");
    }

}

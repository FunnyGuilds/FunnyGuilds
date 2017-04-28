package net.dzikoysk.funnyguilds.util.runnable;

import net.dzikoysk.funnyguilds.FunnyGuilds;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.LinkedList;

public class Ticking implements Runnable {

    private static DecimalFormat df = new DecimalFormat("#,###.##");
    private static String result = "20.0";
    private transient long lastPoll = System.nanoTime();
    private final LinkedList<Double> history = new LinkedList<>();

    private final FunnyGuilds plugin;

    public Ticking(FunnyGuilds plugin) {
        this.plugin = plugin;
        history.add(Double.valueOf(20.0D));
    }

    public void start() {
        this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, this, 1000L, 50L);
    }

    @Override
    public void run() {
        long startTime = System.nanoTime();
        long timeSpent = (startTime - this.lastPoll) / 1000L;
        if (timeSpent == 0L) {
            timeSpent = 1L;
        }
        if (history.size() > 10) {
            history.remove();
        }
        double tps = 50000000.0D / timeSpent;
        if (tps <= 21.0D) {
            history.add(Double.valueOf(tps));
        }
        this.lastPoll = startTime;
        double avg = 0.0D;
        for (Double f : history) {
            if (f != null) {
                avg += f.doubleValue();
            }
        }
        df.setRoundingMode(RoundingMode.HALF_UP);
        result = df.format((avg / history.size()));
    }

    public static String getTPS() {
        return result;
    }
}
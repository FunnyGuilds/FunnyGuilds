package net.dzikoysk.funnyguilds.util.runnable;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.util.reflect.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;

public class Ticker {

    private static final DecimalFormat format = new DecimalFormat("##.##");

    private static Object serverInstance;
    private static Field tpsField;

    static {
        try {
            Class<?> minecraftServerClass = Reflections.getCraftClass("MinecraftServer");
            serverInstance = Reflections.getMethod(Reflections.getCraftClass("MinecraftServer"), "getServer").invoke(null);
            tpsField = Reflections.getField(minecraftServerClass, "recentTps");
        } catch (IllegalAccessException | InvocationTargetException ex) {
            FunnyGuilds.exception(ex.getMessage(), ex.getStackTrace());
        }
    }

    // 0 = last 1 min, 1 = last 5 min, 2 = last 15min
    public static String getRecentTPS(int last) {
        try {
            double[] tps = ((double[]) tpsField.get(serverInstance));
            return format.format(tps[last]);
        } catch (IllegalAccessException ex) {
            FunnyGuilds.exception(ex.getMessage(), ex.getStackTrace());
            return null;
        }
    }

//    private static DecimalFormat df = new DecimalFormat("#,###.##");
//    private static String result = "20.0";
//    private final LinkedList<Double> history = new LinkedList<>();
//    private final FunnyGuilds plugin;
//    private transient long lastPoll = System.nanoTime();


//    public Ticking(FunnyGuilds plugin) {
//        this.plugin = plugin;
//        history.add(Double.valueOf(20.0D));
//    }


//    public static String getTPS() {
//        return result;
//    }
//
//    public void start() {
//        this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, this, 1000L, 50L);
//    }
//
//    @Override
//    public void run() {
//        long startTime = System.nanoTime();
//        long timeSpent = (startTime - this.lastPoll) / 1000L;
//        if (timeSpent == 0L) {
//            timeSpent = 1L;
//        }
//        if (history.size() > 10) {
//            history.remove();
//        }
//        double tps = 50000000.0D / timeSpent;
//        if (tps <= 21.0D) {
//            history.add(Double.valueOf(tps));
//        }
//        this.lastPoll = startTime;
//        double avg = 0.0D;
//        for (Double f : history) {
//            if (f != null) {
//                avg += f.doubleValue();
//            }
//        }
//        df.setRoundingMode(RoundingMode.HALF_UP);
//        result = df.format((avg / history.size()));
//    }
}
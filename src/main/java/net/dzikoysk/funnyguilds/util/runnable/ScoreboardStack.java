package net.dzikoysk.funnyguilds.util.runnable;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.Stack;

public class ScoreboardStack implements Runnable {

    private static ScoreboardStack instance;
    private static Stack<Scoreboard> stack = new Stack<>();

    private final FunnyGuilds plugin;

    public ScoreboardStack(FunnyGuilds plugin) {
        this.plugin = plugin;
        instance = this;
        stack = new Stack<>();
        this.fill();
    }

    public static ScoreboardStack getInstance() {
        try {
            if (instance == null) {
                throw new UnsupportedOperationException("ScoreboardStack is not setup!");
            }
            return instance;
        } catch (Exception ex) {
            if (FunnyGuilds.exception(ex.getCause())) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    public static Scoreboard pull() {
        return stack.pop();
    }

    public static int size() {
        return stack.size();
    }

    @Override
    public void run() {
        fill();
    }

    public void start() {
        this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, this, 0, 20);
    }

    private void fill() {
        int required = Bukkit.getMaxPlayers() * 2;
        if (stack.size() < required) {
            ScoreboardManager sm = Bukkit.getScoreboardManager();
            if (sm == null) {
                FunnyGuilds.error("[ScoreboardStack] ScoreboardManager is null!");
                return;
            }
            int loop = Bukkit.getMaxPlayers() * 2 - stack.size();
            for (int i = 0; i < loop; i++) {
                stack.push(sm.getNewScoreboard());
            }
        }
    }
}

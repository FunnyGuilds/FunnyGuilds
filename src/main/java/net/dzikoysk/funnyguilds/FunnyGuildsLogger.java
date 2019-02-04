package net.dzikoysk.funnyguilds;

import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class FunnyGuildsLogger {

    private final FunnyGuilds funnyGuilds;
    private final Logger      rootLogger;

    FunnyGuildsLogger(FunnyGuilds funnyGuilds) {
        this.funnyGuilds = funnyGuilds;
        this.rootLogger = funnyGuilds.getLogger();
    }

    public void update(String content) {
        this.rootLogger.info("[Updater] > " + content);
    }

    public void parser(String content) {
        this.rootLogger.warning("[Parser] > " + content);
    }

    public void info(String content) {
        this.rootLogger.info(content);
    }

    public void warning(String content) {
        this.rootLogger.warning(content);
    }

    public void error(String content) {
        this.rootLogger.severe(content);
    }

    public void error(String content, Throwable cause) {
        String loadedPlugins = Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .filter(plugin -> ! plugin.getName().contains("FunnyGuilds"))
                .map(plugin -> plugin.getName() + " " + plugin.getDescription().getVersion())
                .collect(Collectors.joining(", "));

        if (loadedPlugins.length() == 0) {
            loadedPlugins = "none";
        }

        error("");
        error(content);
        error("");
        error(cause.toString());
        for (StackTraceElement element : cause.getStackTrace()) {
            error("       at " + element.toString());
        }
        error("");
        error("Server Information:");
        error("  FunnyGuilds: " + this.funnyGuilds.getFullVersion());
        error("  Bukkit: " + Bukkit.getBukkitVersion());
        error("  Java: " + System.getProperty("java.version"));
        error("  Thread: " + Thread.currentThread());
        error("  Loaded plugins: " + loadedPlugins);
        error("");
    }
}
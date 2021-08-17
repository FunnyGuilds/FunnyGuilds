package net.dzikoysk.funnyguilds;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import net.dzikoysk.funnyguilds.shared.bukkit.MinecraftServerUtils;
import org.bukkit.Bukkit;

public final class FunnyGuildsLogger {

    private final FunnyGuilds funnyGuilds;
    private final Logger rootLogger;

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

    public void debug(String content) {
        if (this.funnyGuilds.getPluginConfiguration().debugMode) {
            this.rootLogger.info("[Debug] > " + content);
        }
    }

    public void error(String content) {
        this.rootLogger.severe(content);
    }

    public void error(String content, Throwable cause) {
        String loadedPlugins = Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .filter(plugin -> !plugin.getName().contains("FunnyGuilds"))
                .map(plugin -> plugin.getName() + " " + plugin.getDescription().getVersion())
                .collect(Collectors.joining(", "));

        if (loadedPlugins.length() == 0) {
            loadedPlugins = "none";
        }

        error("");
        error(content);
        error("");

        StringWriter errorDump = new StringWriter();
        cause.printStackTrace(new PrintWriter(errorDump));

        for (String line : errorDump.toString().split("\n")) {
            error(line);
        }

        error("");
        error("Server Information:");
        error("  FunnyGuilds: " + this.funnyGuilds.getVersion().getFullVersion());
        error("  Bukkit: " + Bukkit.getBukkitVersion());
        error("  Java: " + System.getProperty("java.version"));
        error("  Thread: " + Thread.currentThread());
        error("  Loaded plugins: " + loadedPlugins);
        error("  Reload count: " + MinecraftServerUtils.getReloadCount());
        error("");
    }

}
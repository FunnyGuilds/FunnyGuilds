package net.dzikoysk.funnyguilds;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.shared.bukkit.MinecraftServerUtils;
import org.bukkit.Bukkit;

public class FunnyGuildsLogger {

    protected final Logger rootLogger;

    public FunnyGuildsLogger(Logger rootLogger) {
        this.rootLogger = rootLogger;
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
        this.rootLogger.info("[Debug] > " + content);
    }

    public void error(String content) {
        this.rootLogger.severe(content);
    }

    public void error(String content, Throwable cause) {
        error("");
        error(content);
        error("");

        StringWriter errorDump = new StringWriter();
        cause.printStackTrace(new PrintWriter(errorDump));

        for (String line : errorDump.toString().split("\n")) {
            error(line);
        }
    }

    static class DefaultLogger extends FunnyGuildsLogger {

        private final FunnyGuilds plugin;

        DefaultLogger(FunnyGuilds plugin) {
            super(plugin.getLogger());
            this.plugin = plugin;
        }

        @Override
        public void debug(String content) {
            if (this.plugin.getPluginConfiguration().debugMode) {
                return;
            }
            super.debug(content);
        }

        @Override
        public void error(String content, Throwable cause) {
            String loadedPlugins = Arrays.stream(Bukkit.getPluginManager().getPlugins())
                    .filter(plugin -> !plugin.getName().contains("FunnyGuilds"))
                    .map(plugin -> plugin.getName() + " " + plugin.getDescription().getVersion())
                    .collect(Collectors.joining(", "));

            if (loadedPlugins.length() == 0) {
                loadedPlugins = "none";
            }

            super.error(content, cause);

            error("");
            error("Server Information:");
            error("  FunnyGuilds: " + this.plugin.getVersion().getFullVersion());
            error("  Bukkit: " + Bukkit.getBukkitVersion());
            error("  Java: " + System.getProperty("java.version"));
            error("  Thread: " + Thread.currentThread());
            error("  Loaded plugins: " + loadedPlugins);
            error("  Reload count: " + MinecraftServerUtils.getReloadCount());
            error("");
        }

    }

}
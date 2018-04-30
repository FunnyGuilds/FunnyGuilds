package net.dzikoysk.funnyguilds;

import net.dzikoysk.funnyguilds.util.ReloadHandler;
import org.bukkit.Bukkit;

import java.util.logging.Logger;

public final class FunnyLogger {

    private static final Logger bukkitLogger = Bukkit.getLogger();
    
    public static void update(String content) {
        bukkitLogger.info("[FunnyGuilds][Updater] > " + content);
    }

    public static void parser(String content) {
        bukkitLogger.severe("[FunnyGuilds][Parser] #> " + content);
    }

    public static void info(String content) {
        bukkitLogger.info("[FunnyGuilds] " + content);
    }

    public static void warning(String content) {
        bukkitLogger.warning("[FunnyGuilds] " + content);
    }

    public static void error(String content) {
        bukkitLogger.severe("[Server thread/ERROR] #!# " + content);
    }

    public static boolean exception(Throwable cause) {
        return cause == null || exception(cause.getMessage(), cause.getStackTrace());
    }

    public static boolean exception(String cause, StackTraceElement[] ste) {
        error("");
        error("[FunnyGuilds] Severe error:");
        error("");
        error("Server Information:");
        error("  FunnyGuilds: " + FunnyGuilds.getVersion());
        error("  Bukkit: " + Bukkit.getBukkitVersion());
        error("  Java: " + System.getProperty("java.version"));
        error("  Reloads: " + ReloadHandler.getReloadCount());
        error("  Thread: " + Thread.currentThread());
        error("  Running CraftBukkit: " + Bukkit.getServer().getClass().getName().equals("org.bukkit.craftbukkit.CraftServer"));
        error("");
        
        if (cause == null || ste == null || ste.length < 1) {
            error("Stack trace: no/empty exception given, dumping current stack trace instead!");
            return true;
        } else {
            error("Stack trace: ");
        }
        
        error("Caused by: " + cause);
        for (StackTraceElement st : ste) {
            error("    at " + st.toString());
        }
        
        error("");
        error("End of Error.");
        error("");
        return false;
    }

    private FunnyLogger() {}

}
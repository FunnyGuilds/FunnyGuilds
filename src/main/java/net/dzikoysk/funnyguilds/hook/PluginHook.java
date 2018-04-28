package net.dzikoysk.funnyguilds.hook;

import net.dzikoysk.funnyguilds.FunnyLogger;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public final class PluginHook {
    
    public static final String PLUGIN_FUNNYTAB = "FunnyTab";
    public static final String PLUGIN_WORLDGUARD = "WorldGuard";
    public static final String PLUGIN_VAULT = "Vault";
    public static final String PLUGIN_PLACEHOLDERAPI = "PlaceholderAPI";
    
    private static final List<String> HOOK_LIST = new ArrayList<>();

    public static void init() {
        tryInit(PLUGIN_FUNNYTAB, FunnyTabHook::initFunnyDisabler);
        tryInit(PLUGIN_WORLDGUARD, () -> {
            try {
                Class.forName("com.sk89q.worldguard.protection.flags.registry.FlagRegistry");
                Class.forName("com.sk89q.worldguard.protection.flags.Flag");
                WorldGuardHook.initWorldGuard();
            } catch (final ClassNotFoundException e) {
                FunnyLogger.warning("FunnyGuilds supports only WorldGuard v6.2 or newer");
            }
        });

        tryInit(PLUGIN_VAULT, VaultHook::initEconomyHook);
        tryInit(PLUGIN_PLACEHOLDERAPI, PlaceholderAPIHook::initPlaceholderHook);
    }

    public static void tryInit(String plugin, Runnable init) {
        try {
            if (Bukkit.getPluginManager().getPlugin(plugin) != null) {
                init.run();
                HOOK_LIST.add(plugin);
            } else {
                FunnyLogger.info("Plugin: " + plugin + " could not be found, some features may be not available");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isPresent(String plugin) {
        return HOOK_LIST.contains(plugin);
    }
    
}

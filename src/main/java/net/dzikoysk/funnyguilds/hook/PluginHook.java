package net.dzikoysk.funnyguilds.hook;

import net.dzikoysk.funnyguilds.FunnyGuildsLogger;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public final class PluginHook {
    
    public static final String PLUGIN_FUNNYTAB = "FunnyTab";
    public static final String PLUGIN_WORLDGUARD = "WorldGuard";
    public static final String PLUGIN_VAULT = "Vault";
    public static final String PLUGIN_PLACEHOLDERAPI = "PlaceholderAPI";
    public static final String PLUGIN_BUNGEETABLISTPLUS = "BungeeTabListPlus";
    public static final String PLUGIN_MVDWPLACEHOLDERAPI = "MVdWPlaceholderAPI";
    
    private static final List<String> HOOK_LIST = new ArrayList<>();

    public static void init() {
        tryInit(PLUGIN_FUNNYTAB, FunnyTabHook::initFunnyDisabler, false);
        
        tryInit(PLUGIN_WORLDGUARD, () -> {
            try {
                Class.forName("com.sk89q.worldguard.protection.flags.registry.FlagRegistry");
                Class.forName("com.sk89q.worldguard.protection.flags.Flag");
                
                if (Bukkit.getPluginManager().getPlugin("WorldGuard").getDescription().getVersion().startsWith("7")) {
                    FunnyGuildsLogger.warning("Support for WorldGuard v7.0.0 or newer is currently unavailable");
                } else {
                    WorldGuardHook.initWorldGuard();
                }
            } catch (ClassNotFoundException wgTooOld) {
                FunnyGuildsLogger.warning("FunnyGuilds supports only WorldGuard v6.2 or newer");
            }
        });
        
        tryInit(PLUGIN_BUNGEETABLISTPLUS, () -> {
            try {
                Class.forName("codecrafter47.bungeetablistplus.api.bukkit.Variable");
                BungeeTabListPlusHook.initVariableHook();
            }
            catch (ClassNotFoundException ignored) {}
        });
        
        tryInit(PLUGIN_MVDWPLACEHOLDERAPI, () -> {
            try {
                Class.forName("be.maximvdw.placeholderapi.PlaceholderReplacer");
                MVdWPlaceholderAPIHook.initPlaceholderHook();
            } catch (ClassNotFoundException ignored) {}
        });

        tryInit(PLUGIN_VAULT, VaultHook::initEconomyHook);
        tryInit(PLUGIN_PLACEHOLDERAPI, PlaceholderAPIHook::initPlaceholderHook);
    }

    public static void tryInit(String plugin, Runnable init, boolean notifyIfMissing) {
        try {
            if (Bukkit.getPluginManager().getPlugin(plugin) != null) {
                init.run();
                HOOK_LIST.add(plugin);
            } else if (notifyIfMissing) {
                FunnyGuildsLogger.info(plugin + " plugin could not be found, some features may not be available");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void tryInit(String plugin, Runnable init) {
        tryInit(plugin, init, true);
    }

    public static boolean isPresent(String plugin) {
        return HOOK_LIST.contains(plugin);
    }
    
    private PluginHook() {}
    
}

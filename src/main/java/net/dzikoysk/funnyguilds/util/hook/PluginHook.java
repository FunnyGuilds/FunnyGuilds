package net.dzikoysk.funnyguilds.util.hook;

import net.dzikoysk.funnyguilds.util.FunnyLogger;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public final class PluginHook {
    
    public static final String PLUGIN_WORLDGUARD = "WorldGuard";
    public static final String PLUGIN_VAULT = "Vault";
    public static final String PLUGIN_PLACEHOLDESAPI = "PlaceholderAPI";
    public static final String PLUGIN_BUNGEETABLISTPLUS = "BungeeTabListPlus";
    
    private static final List<String> HOOK_LIST = new ArrayList<>();

    public static void init() {
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
        tryInit(PLUGIN_PLACEHOLDESAPI, PlaceholderAPIHook::initPlaceholderHook);
        
        tryInit(PLUGIN_BUNGEETABLISTPLUS, () -> {
            try {
                Class.forName("codecrafter47.bungeetablistplus.api.bukkit.Variable");
                BungeeTabListPlusHook.initVariableHook();
            }
            catch (ClassNotFoundException ignored) {}
        });
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

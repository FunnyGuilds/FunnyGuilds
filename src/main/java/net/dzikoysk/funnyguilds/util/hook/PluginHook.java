package net.dzikoysk.funnyguilds.util.hook;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public final class PluginHook {
    public static final String PLUGIN_WORLDGUARD = "WorldGuard";
    private static final List<String> HOOK_LIST = new ArrayList<>();

    public static void init() {
        tryInit(PLUGIN_WORLDGUARD, () -> {
            try {
                Class.forName("com.sk89q.worldguard.protection.flags.registry.FlagRegistry");
                Class.forName("com.sk89q.worldguard.protection.flags.Flag");
                WorldGuardHook.initWorldGuard();
            } catch (final ClassNotFoundException e) {
                FunnyGuilds.warning("FunnyGuilds supports only WorldGuard v6.2 or newer");
            }
        });
    }

    public static void tryInit(String plugin, Runnable init) {
        try {
            if (Bukkit.getPluginManager().getPlugin(plugin) != null) {
                init.run();
                HOOK_LIST.add(plugin);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isPresent(String plugin) {
        return HOOK_LIST.contains(plugin);
    }
}

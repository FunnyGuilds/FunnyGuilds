package net.dzikoysk.funnyguilds.util.hook;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class PluginHook {
    public static final String PLUGIN_WORLDGUARD = "WorldGuard";
    private static final List<String> hooks = new ArrayList<>();

    @SuppressWarnings("Convert2MethodRef")
    public static void init() {
        tryInit(PLUGIN_WORLDGUARD, () -> {
            try {
                Class.forName("com.sk89q.worldguard.protection.flags.registry.FlagRegistry");
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
                hooks.add(plugin);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isPresent(String plugin) {
        return hooks.contains(plugin);
    }
}

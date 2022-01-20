package net.dzikoysk.funnyguilds.feature.hooks;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.hooks.bungeetablist.BungeeTabListPlusHook;
import net.dzikoysk.funnyguilds.feature.hooks.funnytab.FunnyTabHook;
import net.dzikoysk.funnyguilds.feature.hooks.hologram.HologramHook;
import net.dzikoysk.funnyguilds.feature.hooks.hologram.HolographicDisplaysHook;
import net.dzikoysk.funnyguilds.feature.hooks.leaderheads.LeaderHeadsHook;
import net.dzikoysk.funnyguilds.feature.hooks.mvdwplaceholderapi.MVdWPlaceholderAPIHook;
import net.dzikoysk.funnyguilds.feature.hooks.placeholderapi.PlaceholderAPIHook;
import net.dzikoysk.funnyguilds.feature.hooks.vault.VaultHook;
import net.dzikoysk.funnyguilds.feature.hooks.worldedit.WorldEdit6Hook;
import net.dzikoysk.funnyguilds.feature.hooks.worldedit.WorldEdit7Hook;
import net.dzikoysk.funnyguilds.feature.hooks.worldedit.WorldEditHook;
import net.dzikoysk.funnyguilds.feature.hooks.worldguard.WorldGuard6Hook;
import net.dzikoysk.funnyguilds.feature.hooks.worldguard.WorldGuard7Hook;
import net.dzikoysk.funnyguilds.feature.hooks.worldguard.WorldGuardHook;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import panda.std.Option;
import panda.std.reactive.Completable;
import panda.std.stream.PandaStream;

public class HookManager {

    public static Option<WorldGuardHook> WORLD_GUARD;
    public static Option<WorldEditHook> WORLD_EDIT;
    public static Option<FunnyTabHook> FUNNY_TAB;
    public static Option<VaultHook> VAULT;
    public static Option<BungeeTabListPlusHook> BUNGEE_TAB_LIST_PLUS;
    public static Option<MVdWPlaceholderAPIHook> MVDW_PLACEHOLDER_API;
    public static Option<PlaceholderAPIHook> PLACEHOLDER_API;
    public static Option<LeaderHeadsHook> LEADER_HEADS;
    public static Option<HologramHook> HOLOGRAPHIC_DISPLAYS;

    private final FunnyGuilds plugin;
    private final Map<String, CompletableHook<?>> pluginHooks = new HashMap<>();

    public HookManager(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    public void setupEarlyHooks() {
        this.setupHook("WorldGuard", false, pluginName -> {
            try {
                Class.forName("com.sk89q.worldguard.protection.flags.registry.FlagRegistry");
                Class.forName("com.sk89q.worldguard.protection.flags.Flag");

                String worldGuardVersion = Bukkit.getPluginManager().getPlugin(pluginName).getDescription().getVersion();
                return worldGuardVersion.startsWith("7") ? new WorldGuard7Hook(pluginName) : new WorldGuard6Hook(pluginName);
            }
            catch (ClassNotFoundException exception) {
                FunnyGuilds.getPluginLogger().warning("FunnyGuilds supports only WorldGuard v6.2 or newer");
                return null;
            }
        }, true).subscribe(hook -> WORLD_GUARD = hook);

        this.setupHook("FunnyTab", false, pluginName -> new FunnyTabHook(pluginName, plugin), false)
                .subscribe(hook -> FUNNY_TAB = hook);
    }

    public void setupHooks() {
        this.setupHook("WorldEdit", true, pluginName -> {
            try {
                Class.forName("com.sk89q.worldedit.Vector");
                return new WorldEdit6Hook(pluginName);
            }
            catch (ClassNotFoundException exception) {
                return new WorldEdit7Hook(pluginName);
            }
        }, true).subscribe(hook -> WORLD_EDIT = hook);

        this.setupHook("BungeeTabListPlus", true, pluginName -> {
            try {
                Class.forName("codecrafter47.bungeetablistplus.api.bukkit.Variable");
                return new BungeeTabListPlusHook(pluginName, plugin);
            }
            catch (ClassNotFoundException exception) {
                return null;
            }
        }, true).subscribe(hook -> BUNGEE_TAB_LIST_PLUS = hook);

        this.setupHook("Vault", true, VaultHook::new, true)
                .subscribe(hook -> VAULT = hook);

        this.setupHook("MVdWPlaceholderAPI", true, pluginName -> new MVdWPlaceholderAPIHook(pluginName, plugin), true)
                .subscribe(hook -> MVDW_PLACEHOLDER_API = hook);

        this.setupHook("PlaceholderAPI", true, pluginName -> new PlaceholderAPIHook(pluginName, plugin), true)
                .subscribe(hook -> PLACEHOLDER_API = hook);

        this.setupHook("LeaderHeads", true, pluginName -> new LeaderHeadsHook(pluginName, plugin), true)
                .subscribe(hook -> LEADER_HEADS = hook);

        this.<HologramHook>setupHook("HolographicDisplays", true, pluginName -> new HolographicDisplaysHook(pluginName, plugin), true)
                .subscribe(hook -> HOLOGRAPHIC_DISPLAYS = hook);
    }

    public <T extends PluginHook> Completable<Option<T>> setupHook(String pluginName, boolean requireEnabled,
                                            Function<String, T> hookSupplier, boolean notifyIfMissing) {
        if (hookSupplier == null) {
            return Completable.completed(Option.none());
        }

        Plugin hookPlugin = Bukkit.getPluginManager().getPlugin(pluginName);

        if (hookPlugin == null || (requireEnabled && !hookPlugin.isEnabled())) {
            if (notifyIfMissing) {
                FunnyGuilds.getPluginLogger().info(pluginName + " plugin could not be found, some features may not be available");
            }

            return Completable.completed(Option.none());
        }

        T hook = hookSupplier.apply(pluginName);

        if (hook == null) {
            return Completable.completed(Option.none());
        }

        if (PandaStream.of(plugin.getPluginConfiguration().disabledHooks)
                .find(disabledHook -> disabledHook.equalsIgnoreCase(pluginName))
                .isPresent()) {
            if (!pluginName.equalsIgnoreCase("FunnyTab")) {
                FunnyGuilds.getPluginLogger().warning(pluginName + " plugin hook is disabled in configuration, some features may not be available");
                return Completable.completed(Option.none());
            }

            FunnyGuilds.getPluginLogger().warning("You can't disable FunnyTab plugin hook lol");
        }

        Completable<Option<T>> hookCompletable = new Completable<>();

        this.pluginHooks.put(pluginName, new CompletableHook<>(hook, hookCompletable));

        return hookCompletable;
    }

    public void earlyInit() {
        pluginHooks.forEach((pluginName, completableHook) -> {
            if (completableHook.isReady()) {
                return;
            }

            try {
                completableHook.earlyInit();
            }
            catch (Exception exception) {
                completableHook.markAsNotCompleted();

                FunnyGuilds.getPluginLogger().error("Failed to early initialize " + pluginName + " plugin hook");
                exception.printStackTrace();
            }
        });
    }

    public void init() {
        pluginHooks.forEach((pluginName, completableHook) -> {
            if (completableHook.isReady()) {
                return;
            }

            try {
                completableHook.init();
                completableHook.markAsCompleted();

                FunnyGuilds.getPluginLogger().info(pluginName + " plugin hook has been enabled!");
            }
            catch (Exception exception) {
                completableHook.markAsNotCompleted();

                FunnyGuilds.getPluginLogger().error("Failed to initialize " + pluginName + " plugin hook");
                exception.printStackTrace();
            }
        });
    }

}

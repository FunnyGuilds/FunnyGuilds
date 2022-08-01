package net.dzikoysk.funnyguilds.feature.hooks;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.holograms.HologramsHook;
import net.dzikoysk.funnyguilds.feature.hooks.bungeetablist.BungeeTabListPlusHook;
import net.dzikoysk.funnyguilds.feature.hooks.funnytab.FunnyTabHook;
import net.dzikoysk.funnyguilds.feature.hooks.holographicdisplays.HolographicDisplaysHook;
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

    public static Option<WorldGuardHook> WORLD_GUARD = Option.none();
    public static Option<WorldEditHook> WORLD_EDIT = Option.none();
    public static Option<FunnyTabHook> FUNNY_TAB = Option.none();
    public static Option<VaultHook> VAULT = Option.none();
    public static Option<BungeeTabListPlusHook> BUNGEE_TAB_LIST_PLUS = Option.none();
    public static Option<PlaceholderAPIHook> PLACEHOLDER_API = Option.none();
    public static Option<HologramsHook> HOLOGRAMS = Option.none();

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

        this.setupHook("FunnyTab", false, pluginName -> new FunnyTabHook(pluginName, this.plugin), false)
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
                return new BungeeTabListPlusHook(pluginName, this.plugin);
            }
            catch (ClassNotFoundException exception) {
                return null;
            }
        }, true).subscribe(hook -> BUNGEE_TAB_LIST_PLUS = hook);

        this.setupHook("Vault", true, VaultHook::new, true)
                .subscribe(hook -> VAULT = hook);

        this.setupHook("PlaceholderAPI", true, pluginName -> new PlaceholderAPIHook(pluginName, this.plugin), true)
                .subscribe(hook -> PLACEHOLDER_API = hook);

        this.<HologramsHook>setupHook("HolographicDisplays", true, pluginName -> new HolographicDisplaysHook(pluginName, this.plugin), true)
                .subscribe(hook -> HOLOGRAMS = hook);
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

        PandaStream<String> disabledHooks = PandaStream.of(this.plugin.getPluginConfiguration().disabledHooks);
        if (disabledHooks.find(disabledHook -> disabledHook.equalsIgnoreCase(pluginName)).isPresent()) {
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
        this.pluginHooks.forEach((pluginName, completableHook) -> {
            if (completableHook.isCompleted()) {
                return;
            }

            try {
                PluginHook.HookInitResult result = completableHook.earlyInit();
                if (result == PluginHook.HookInitResult.UNUSED) {
                    return;
                }

                if (result == PluginHook.HookInitResult.FAILURE) {
                    FunnyGuilds.getPluginLogger().error("Failed to early initialize " + pluginName + " plugin hook");
                    completableHook.markAsNotCompleted();
                    return;
                }

                FunnyGuilds.getPluginLogger().info(pluginName + " plugin hook has been early initialized!");
            }
            catch (Throwable throwable) {
                FunnyGuilds.getPluginLogger().error("Failed to early initialize " + pluginName + " plugin hook", throwable);
                completableHook.markAsNotCompleted();
            }
        });
    }

    public void init() {
        this.pluginHooks.forEach((pluginName, completableHook) -> {
            if (completableHook.isCompleted()) {
                return;
            }

            try {
                PluginHook.HookInitResult result = completableHook.init();

                if (result == PluginHook.HookInitResult.FAILURE) {
                    FunnyGuilds.getPluginLogger().error("Failed to initialize " + pluginName + " plugin hook");
                    completableHook.markAsNotCompleted();
                    return;
                }

                if (result != PluginHook.HookInitResult.UNUSED) {
                    FunnyGuilds.getPluginLogger().info(pluginName + " plugin hook has been initialized!");
                }

                completableHook.markAsCompleted();
            }
            catch (Throwable throwable) {
                FunnyGuilds.getPluginLogger().error("Failed to initialize " + pluginName + " plugin hook", throwable);
                completableHook.markAsNotCompleted();
            }
        });
    }

    public void callConfigUpdated() {
        this.pluginHooks.forEach((pluginName, completableHook) -> {
            if (!completableHook.isCompleted()) {
                return;
            }

            try {
                completableHook.configUpdated();
            }
            catch (Throwable throwable) {
                FunnyGuilds.getPluginLogger().error("Failed to invoke configUpdated() for " + pluginName + " plugin hook", throwable);
            }
        });
    }

}

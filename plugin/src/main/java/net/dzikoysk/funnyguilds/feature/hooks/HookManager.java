package net.dzikoysk.funnyguilds.feature.hooks;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.holograms.HologramsHook;
import net.dzikoysk.funnyguilds.feature.hooks.decentholograms.DecentHologramsHook;
import net.dzikoysk.funnyguilds.feature.hooks.dynmap.DynmapHook;
import net.dzikoysk.funnyguilds.feature.hooks.placeholderapi.PlaceholderAPIHook;
import net.dzikoysk.funnyguilds.feature.hooks.skript.SkriptHook;
import net.dzikoysk.funnyguilds.feature.hooks.vault.VaultHook;
import net.dzikoysk.funnyguilds.feature.hooks.worldedit.WorldEdit7Hook;
import net.dzikoysk.funnyguilds.feature.hooks.worldedit.WorldEditHook;
import net.dzikoysk.funnyguilds.feature.hooks.worldguard.WorldGuard7Hook;
import net.dzikoysk.funnyguilds.feature.hooks.worldguard.WorldGuardHook;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import panda.std.reactive.Completable;

public class HookManager {

    public static Optional<WorldGuardHook> WORLD_GUARD = Optional.empty();
    public static Optional<WorldEditHook> WORLD_EDIT = Optional.empty();
    public static Optional<VaultHook> VAULT = Optional.empty();
    public static Optional<PlaceholderAPIHook> PLACEHOLDER_API = Optional.empty();
    public static Optional<HologramsHook> HOLOGRAMS = Optional.empty();
    public static Optional<DynmapHook> DYNMAP = Optional.empty();

    private final FunnyGuilds plugin;
    private final Map<String, CompletableHook<?>> pluginHooks = new HashMap<>();

    public HookManager(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    public void setupEarlyHooks() {
        this.<WorldGuardHook>setupHook(
                "WorldGuard",
                false,
                pluginName -> new WorldGuard7Hook(pluginName),
                true
        ).subscribe(hook -> WORLD_GUARD = hook);
    }

    public void setupHooks() {
        this.<WorldEditHook>setupHook(
                "WorldEdit",
                false,
                pluginName -> new WorldEdit7Hook(pluginName),
                true
        ).subscribe(hook -> WORLD_EDIT = hook);

        this.setupHook("Vault", true, VaultHook::new, true)
                .subscribe(hook -> VAULT = hook);

        this.setupHook("PlaceholderAPI", true, pluginName -> new PlaceholderAPIHook(pluginName, this.plugin), true)
                .subscribe(hook -> PLACEHOLDER_API = hook);

        this.<HologramsHook>setupHook("DecentHolograms", true, pluginName -> new DecentHologramsHook(pluginName, this.plugin), true)
                .subscribe(hook -> hook.ifPresent(dhHook -> HOLOGRAMS = Optional.of(dhHook)));

        this.setupHook("dynmap", true, pluginName -> new DynmapHook(pluginName, this.plugin), true)
                .subscribe(hook -> DYNMAP = hook);

        this.setupHook("Skript", true, pluginName -> new SkriptHook(pluginName, this.plugin), false);
    }

    public <T extends PluginHook> Completable<Optional<T>> setupHook(String pluginName, boolean requireEnabled,
                                                                     Function<String, T> hookSupplier, boolean notifyIfMissing) {
        if (hookSupplier == null) {
            return Completable.completed(Optional.empty());
        }

        Plugin hookPlugin = Bukkit.getPluginManager().getPlugin(pluginName);
        if (hookPlugin == null || (requireEnabled && !hookPlugin.isEnabled())) {
            if (notifyIfMissing) {
                FunnyGuilds.getPluginLogger().info(pluginName + " plugin could not be found, some features may not be available");
            }

            return Completable.completed(Optional.empty());
        }

        T hook = hookSupplier.apply(pluginName);
        if (hook == null) {
            return Completable.completed(Optional.empty());
        }

        if (this.plugin.getPluginConfiguration().disabledHooks.stream()
                .anyMatch(disabledHook -> disabledHook.equalsIgnoreCase(pluginName))) {
            if (!pluginName.equalsIgnoreCase("FunnyTab")) {
                FunnyGuilds.getPluginLogger().warning(pluginName + " plugin hook is disabled in configuration, some features may not be available");
                return Completable.completed(Optional.empty());
            }

            FunnyGuilds.getPluginLogger().warning("You can't disable FunnyTab plugin hook lol");
        }

        Completable<Optional<T>> hookCompletable = new Completable<>();
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

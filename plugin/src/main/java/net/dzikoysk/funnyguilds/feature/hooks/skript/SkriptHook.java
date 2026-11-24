package net.dzikoysk.funnyguilds.feature.hooks.skript;

import java.lang.reflect.Method;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.hooks.AbstractPluginHook;
import net.dzikoysk.funnyguilds.feature.hooks.PluginHook;

/**
 * Initializes the bundled Skript extension (FunnySkAddon) when the Skript plugin is present.
 * The extension module compiles against the plugin classes, so it is wired in through
 * reflection to avoid a compile-time dependency cycle.
 */
public class SkriptHook extends AbstractPluginHook {

    private final FunnyGuilds plugin;

    public SkriptHook(String name, FunnyGuilds plugin) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public PluginHook.HookInitResult init() throws Throwable {
        Class<?> extensionClass = Class.forName("pl.funnyskaddon.FunnySkAddon");
        Method register = extensionClass.getMethod("register", FunnyGuilds.class);
        register.invoke(null, this.plugin);
        return PluginHook.HookInitResult.SUCCESS;
    }

}

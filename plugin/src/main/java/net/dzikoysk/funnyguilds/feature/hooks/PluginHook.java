package net.dzikoysk.funnyguilds.feature.hooks;

public interface PluginHook {

    String getName();

    default HookInitResult earlyInit() throws Throwable {
        return HookInitResult.UNUSED;
    }

    default HookInitResult init() throws Throwable {
        return HookInitResult.UNUSED;
    }

    enum HookInitResult {
        SUCCESS, FAILURE, UNUSED
    }

}

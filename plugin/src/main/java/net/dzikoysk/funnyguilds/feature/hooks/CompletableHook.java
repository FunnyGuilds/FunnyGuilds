package net.dzikoysk.funnyguilds.feature.hooks;

import panda.std.Option;
import panda.std.reactive.Completable;

public class CompletableHook<T extends PluginHook> {

    private final T hook;
    private final Completable<Option<T>> completableHook;

    public CompletableHook(T hook, Completable<Option<T>> completableHook) {
        this.hook = hook;
        this.completableHook = completableHook;
    }

    public PluginHook.HookInitResult earlyInit() throws Throwable {
        return hook.earlyInit();
    }

    public PluginHook.HookInitResult init() throws Throwable {
        return hook.init();
    }

    public void configUpdated() throws Throwable {
        hook.configUpdated();
    }

    public void markAsCompleted() {
        completableHook.complete(Option.of(hook));
    }

    public void markAsNotCompleted() {
        completableHook.complete(Option.none());
    }

    public boolean isCompleted() {
        return completableHook.isReady();
    }

}

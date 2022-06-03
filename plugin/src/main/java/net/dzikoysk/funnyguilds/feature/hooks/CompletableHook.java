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
        return this.hook.earlyInit();
    }

    public PluginHook.HookInitResult init() throws Throwable {
        return this.hook.init();
    }

    public void configUpdated() throws Throwable {
        this.hook.configUpdated();
    }

    public void markAsCompleted() {
        this.completableHook.complete(Option.of(this.hook));
    }

    public void markAsNotCompleted() {
        this.completableHook.complete(Option.none());
    }

    public boolean isCompleted() {
        return this.completableHook.isReady();
    }

}

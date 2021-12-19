package net.dzikoysk.funnyguilds.feature.hooks;

public abstract class AbstractPluginHook implements PluginHook {

    private final String name;

    protected boolean earlyInitialized;
    protected boolean initialized;

    protected AbstractPluginHook(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isEarlyInitialized() {
        return this.earlyInitialized;
    }

    @Override
    public final void callEarlyInit() {
        this.earlyInit();
        this.earlyInitialized = true;
    }

    public void earlyInit() {
    }

    @Override
    public boolean isInitialized() {
        return this.initialized;
    }

    @Override
    public final void callInit() {
        this.init();
        this.initialized = true;
    }

    public void init() {
    }

}

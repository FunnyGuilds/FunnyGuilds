package net.dzikoysk.funnyguilds.feature.hooks;

public abstract class AbstractPluginHook implements PluginHook {

    private final String name;

    protected boolean present;
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
    public boolean isPresent() {
        return present;
    }

    @Override
    public void setPresent(boolean present) {
        this.present = present;
    }

    @Override
    public boolean isEarlyInitialized() {
        return this.earlyInitialized;
    }

    @Override
    public void earlyInit() {
        this.earlyInitialized = true;
    }

    @Override
    public boolean isInitialized() {
        return this.initialized;
    }

    @Override
    public void init() {
        this.initialized = true;
    }

}

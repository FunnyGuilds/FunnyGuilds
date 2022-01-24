package net.dzikoysk.funnyguilds.feature.hooks;

public abstract class AbstractPluginHook implements PluginHook {

    private final String name;

    protected AbstractPluginHook(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

}

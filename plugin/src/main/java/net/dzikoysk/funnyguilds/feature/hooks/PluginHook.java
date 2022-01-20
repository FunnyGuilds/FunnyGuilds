package net.dzikoysk.funnyguilds.feature.hooks;

public interface PluginHook {

    String getName();

    void earlyInit();

    void init();

}

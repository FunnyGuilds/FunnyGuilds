package net.dzikoysk.funnyguilds.feature.hooks;

public interface PluginHook {

    String getName();

    boolean isEarlyInitialized();

    void callEarlyInit();

    boolean isInitialized();

    void callInit();

    default boolean isFullyInitialized() {
        return isEarlyInitialized() && isInitialized();
    }

}

package net.dzikoysk.funnyguilds.feature.hooks;

public interface PluginHook {

    String getName();

    boolean isPresent();

    default void setPresent(boolean present) {}

    boolean isEarlyInitialized();

    void earlyInit();

    boolean isInitialized();

    void init();

    default boolean isFullyInitialized() {
        return isPresent() && isEarlyInitialized() && isInitialized();
    }

}

package net.dzikoysk.funnyguilds.system.event;

import java.util.ArrayList;
import java.util.List;

public class EventManager {

    private static EventManager instance;
    private List<EventExtension> extensions;
    private EventSettings settings;

    public EventManager() {
        instance = this;
        settings = EventSettings.getInstance();
        extensions = new ArrayList<>();
        settings.hashCode();
    }

    public final void load() {
        for (EventExtension ee : extensions) {
            ee.onLoad();
        }
    }

    public final void enable() {
        for (EventExtension ee : extensions) {
            ee.onEnable();
        }
    }

    public final void disable() {
        for (EventExtension ee : extensions) {
            ee.onDisable();
        }
    }

    public List<EventExtension> getExtensions() {
        return extensions;
    }

    public static EventManager getEventManager() {
        if (instance == null) {
            new EventManager();
        }
        return instance;
    }
}

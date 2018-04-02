package net.dzikoysk.funnyguilds.system.event;

import java.util.ArrayList;
import java.util.List;

public class EventManager {

    private final List<EventExtension> extensions;

    public EventManager() {
        this.extensions = new ArrayList<>();
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

}

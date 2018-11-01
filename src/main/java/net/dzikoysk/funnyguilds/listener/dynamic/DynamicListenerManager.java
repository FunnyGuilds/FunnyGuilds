package net.dzikoysk.funnyguilds.listener.dynamic;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Supplier;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.bukkit.event.Listener;

public class DynamicListenerManager {
    private final Set<DynamicListenerRegistration> registrations = new HashSet<>();
    private final FunnyGuilds funnyGuilds;

    public DynamicListenerManager(FunnyGuilds funnyGuilds) {
        this.funnyGuilds = funnyGuilds;
    }

    public boolean registerDynamic(Supplier<Boolean> predicate, Listener... listeners) {
        return this.registrations.add(new DynamicListenerRegistration(this.funnyGuilds, Arrays.asList(listeners), predicate));
    }

    public boolean unregister(Listener listener) {
        Iterator<DynamicListenerRegistration> iter = this.registrations.iterator();

        boolean result = false;

        while (iter.hasNext()) {
            DynamicListenerRegistration next = iter.next();

            if (!next.getListeners().remove(listener)) {
                continue;
            }

            result = true;

            if (next.getListeners().isEmpty()) {
                iter.remove();
            }
        }

        return result;
    }

    public void reloadAll() {
        this.registrations.forEach(DynamicListenerRegistration::reload);
    }

    public void unregisterAll() {
        this.registrations.forEach(DynamicListenerRegistration::forceUnregister);
        this.registrations.clear();
    }
}

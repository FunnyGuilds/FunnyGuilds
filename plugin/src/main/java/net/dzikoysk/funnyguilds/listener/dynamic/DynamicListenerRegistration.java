package net.dzikoysk.funnyguilds.listener.dynamic;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.apache.commons.lang3.Validate;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

class DynamicListenerRegistration {

    private final FunnyGuilds funnyGuilds;
    private final Collection<Listener> listeners;
    private final Supplier<Boolean>  predicate;
    private boolean currentState;

    public DynamicListenerRegistration(FunnyGuilds funnyGuilds, Collection<Listener> listeners, Supplier<Boolean> predicate) {
        this.funnyGuilds = funnyGuilds;
        this.listeners = Validate.notNull(listeners, "listener");
        this.predicate = Validate.notNull(predicate, "predicate");
    }

    public void reload() {
        boolean requiredState = this.predicate.get();

        if (requiredState == this.currentState) {
            return;
        }

        if (requiredState) {
            this.forceRegister();
        } else {
            this.forceUnregister();
        }
    }

    public void forceRegister() {
        this.listeners.forEach(listener -> funnyGuilds.getServer().getPluginManager().registerEvents(listener, this.funnyGuilds));
        this.currentState = true;
    }

    public void forceUnregister() {
        this.listeners.forEach(HandlerList::unregisterAll);
        this.currentState = false;
    }

    public Collection<Listener> getListeners() {
        return this.listeners;
    }

}

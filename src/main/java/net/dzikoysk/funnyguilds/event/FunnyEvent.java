package net.dzikoysk.funnyguilds.event;

import net.dzikoysk.funnyguilds.basic.user.User;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public abstract class FunnyEvent extends Event implements Cancellable {

    private final EventCause eventCause;
    private final User doer;
    
    private String cancelMessage;
    private boolean cancelled;
    
    public FunnyEvent(EventCause eventCause, User doer) {
        this.eventCause = eventCause;
        this.doer = doer;
    }

    public FunnyEvent(EventCause eventCause, User doer, boolean isAsync) {
        super(isAsync);
        this.eventCause = eventCause;
        this.doer = doer;
    }
    
    public EventCause getEventCause() {
        return this.eventCause;
    }
    
    public User getDoer() {
        return this.doer;
    }
    
    public abstract String getDefaultCancelMessage();
    
    public String getCancelMessage() {
        if(this.cancelMessage == null || this.cancelMessage.isEmpty()) {
            return getDefaultCancelMessage();
        }
        
        return this.cancelMessage;
    }

    public void setCancelMessage(String cancelMessage) {
        this.cancelMessage = cancelMessage;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public void notifyDoer() {
        if (this.doer !=null && this.doer.isOnline()) {
            this.doer.getPlayer().sendMessage(getCancelMessage());
        }
    }

    public enum EventCause {

        ADMIN,
        CONSOLE,
        SYSTEM,
        USER,
        UNKNOWN;

    }
    
}

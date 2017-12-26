package net.dzikoysk.funnyguilds.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.dzikoysk.funnyguilds.basic.User;

public abstract class FunnyEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    
    private final EventCause eventCause;
    private final User doer;
    
    private String cancelMessage;
    private boolean cancelled;
    
    public FunnyEvent(EventCause eventCause, User doer) {
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
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlersList() {
        return handlers;
    }
    
    public void notifyDoer() {
        if (this.doer !=null && this.doer.isOnline()) {
            this.doer.getPlayer().sendMessage(getCancelMessage());
        }
    }

    public enum EventCause {
        ADMIN, CONSOLE, SYSTEM, USER, UNKNOWN;
    }
    
}

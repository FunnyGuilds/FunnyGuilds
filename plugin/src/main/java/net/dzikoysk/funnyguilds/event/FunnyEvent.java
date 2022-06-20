package net.dzikoysk.funnyguilds.event;

import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;
import panda.utilities.StringUtils;

public abstract class FunnyEvent extends Event implements Cancellable {

    private final EventCause eventCause;
    private final Option<User> doer;
    private String cancelMessage;
    private boolean cancelled;

    public FunnyEvent(EventCause eventCause, @Nullable User doer) {
        this.eventCause = eventCause;
        this.doer = Option.of(doer);
    }

    public FunnyEvent(EventCause eventCause, @Nullable User doer, boolean isAsync) {
        super(isAsync);
        this.eventCause = eventCause;
        this.doer = Option.of(doer);
    }

    public EventCause getEventCause() {
        return this.eventCause;
    }

    public Option<User> getDoer() {
        return this.doer;
    }

    public abstract String getDefaultCancelMessage();

    //TODO: javadocs
    public String getCancelMessage() {
        if (StringUtils.isEmpty(this.cancelMessage)) {
            return this.getDefaultCancelMessage();
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

    void notifyDoer() {
        this.doer.peek(user -> user.sendMessage(this.getCancelMessage()));
    }

    public enum EventCause {

        ADMIN,
        CONSOLE,
        SYSTEM,
        USER,
        UNKNOWN

    }

}

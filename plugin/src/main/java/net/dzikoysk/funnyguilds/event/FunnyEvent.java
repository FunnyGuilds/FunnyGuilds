package net.dzikoysk.funnyguilds.event;

import java.util.Optional;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public abstract class FunnyEvent extends Event implements Cancellable {

    private final EventCause eventCause;
    private final Optional<User> doer;
    private String cancelMessage;
    private boolean cancelled;

    public FunnyEvent(EventCause eventCause, @Nullable User doer) {
        this.eventCause = eventCause;
        this.doer = Optional.ofNullable(doer);
    }

    public FunnyEvent(EventCause eventCause, @Nullable User doer, boolean isAsync) {
        super(isAsync);
        this.eventCause = eventCause;
        this.doer = Optional.ofNullable(doer);
    }

    public EventCause getEventCause() {
        return this.eventCause;
    }

    /**
     * @return the doer of the event, if doer wasn't a user the Optional will be empty
     */
    public Optional<User> getDoer() {
        return this.doer;
    }

    public abstract String getDefaultCancelMessage();

    /**
     * The message that will be displayed for event doer when the event is cancelled.
     *
     * @return the set cancel message, if null the default cancel message will be returned.
     */
    public String getCancelMessage() {
        if (FunnyStringUtils.isEmpty(this.cancelMessage)) {
            return this.getDefaultCancelMessage();
        }

        return this.cancelMessage;
    }

    /**
     * Sets the message that will be displayed for event doer when the event is cancelled.
     *
     * @param cancelMessage the cancel message
     */
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
        this.doer.ifPresent(user -> user.sendMessage(this.getCancelMessage()));
    }

    public enum EventCause {

        ADMIN,
        CONSOLE,
        SYSTEM,
        USER,
        COMBAT,
        UNKNOWN

    }

}

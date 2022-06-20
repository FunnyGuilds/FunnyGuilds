package net.dzikoysk.funnyguilds.event.rank;

import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LogoutsChangeEvent extends AbstractRankEvent {

    private static final HandlerList handlers = new HandlerList();
    private int logoutsChange;

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public LogoutsChangeEvent(EventCause eventCause, User doer, User affected, int logoutsChange) {
        super(eventCause, doer, affected);
        this.logoutsChange = logoutsChange;
    }

    public int getLogoutsChange() {
        return this.logoutsChange;
    }

    public void setLogoutsChange(int logoutsChange) {
        this.logoutsChange = logoutsChange;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Logouts change has been cancelled by the server!";
    }

}

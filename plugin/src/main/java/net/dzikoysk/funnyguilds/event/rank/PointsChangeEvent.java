package net.dzikoysk.funnyguilds.event.rank;

import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;

public class PointsChangeEvent extends RankChangeEvent {

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public PointsChangeEvent(EventCause eventCause, User doer, User affected, int pointsChange) {
        super(eventCause, doer, affected, pointsChange);
    }

    public int getPointsChange() {
        return this.change;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Points change has been cancelled by the server!";
    }

}

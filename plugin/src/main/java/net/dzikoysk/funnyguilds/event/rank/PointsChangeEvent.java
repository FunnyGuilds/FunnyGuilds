package net.dzikoysk.funnyguilds.event.rank;

import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;

public class PointsChangeEvent extends AbstractRankEvent {

    private static final HandlerList handlers = new HandlerList();

    private int pointsChange;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public PointsChangeEvent(EventCause eventCause, User doer, User affected, int pointsChange) {
        super(eventCause, doer, affected);

        this.pointsChange = pointsChange;
    }

    public int getPointsChange() {
        return pointsChange;
    }

    public void setPointsChange(int pointsChange) {
        this.pointsChange = pointsChange;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Points change has been cancelled by the server!";
    }

}

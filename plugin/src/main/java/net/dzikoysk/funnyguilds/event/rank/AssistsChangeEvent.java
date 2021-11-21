package net.dzikoysk.funnyguilds.event.rank;

import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;

public class AssistsChangeEvent extends RankChangeEvent {

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public AssistsChangeEvent(EventCause eventCause, User doer, User affected, int assistsChange) {
        super(eventCause, doer, affected, assistsChange);
    }

    public int getAssistsChange() {
        return this.change;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Assists change has been cancelled by the server!";
    }

}

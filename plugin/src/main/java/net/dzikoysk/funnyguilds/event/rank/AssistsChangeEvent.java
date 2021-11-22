package net.dzikoysk.funnyguilds.event.rank;

import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;

public class AssistsChangeEvent extends AbstractRankEvent {

    private static final HandlerList handlers = new HandlerList();

    private int assistsChange;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public AssistsChangeEvent(EventCause eventCause, User doer, User affected, int assistsChange) {
        super(eventCause, doer, affected);

        this.assistsChange = assistsChange;
    }

    public int getAssistsChange() {
        return assistsChange;
    }

    public void setAssistsChange(int assistsChange) {
        this.assistsChange = assistsChange;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Assists change has been cancelled by the server!";
    }

}

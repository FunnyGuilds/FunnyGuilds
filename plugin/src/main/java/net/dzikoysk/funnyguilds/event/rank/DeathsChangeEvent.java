package net.dzikoysk.funnyguilds.event.rank;

import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;

public class DeathsChangeEvent extends AbstractRankEvent {

    private static final HandlerList handlers = new HandlerList();

    private int deathsChange;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public DeathsChangeEvent(EventCause eventCause, User doer, User affected, int deathsChange) {
        super(eventCause, doer, affected);

        this.deathsChange = deathsChange;
    }

    public int getDeathsChange() {
        return deathsChange;
    }

    public void setDeathsChange(int deathsChange) {
        this.deathsChange = deathsChange;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Deaths change has been cancelled by the server!";
    }

}

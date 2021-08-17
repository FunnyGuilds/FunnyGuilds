package net.dzikoysk.funnyguilds.event.rank;

import net.dzikoysk.funnyguilds.rank.Rank;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;

public class DeathsChangeEvent extends RankChangeEvent {

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public DeathsChangeEvent(EventCause eventCause, Rank rank, User doer, int change) {
        super(eventCause, rank, doer, change);
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Deaths change has been cancelled by the server!";
    }
    
}

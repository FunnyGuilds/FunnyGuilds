package net.dzikoysk.funnyguilds.event.rank;

import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;

public class KillsChangeEvent extends RankChangeEvent {

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public KillsChangeEvent(EventCause eventCause, User doer, User affected, int change) {
        super(eventCause, doer, affected, change);
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Kills change has been cancelled by the server!";
    }

}

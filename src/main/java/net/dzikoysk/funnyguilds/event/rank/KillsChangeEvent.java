package net.dzikoysk.funnyguilds.event.rank;

import net.dzikoysk.funnyguilds.basic.Rank;
import net.dzikoysk.funnyguilds.basic.User;

public class KillsChangeEvent extends RankChangeEvent {

    public KillsChangeEvent(EventCause eventCause, Rank rank, User doer, int change) {
        super(eventCause, rank, doer, change);
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Kills change has been cancelled by the server!";
    }
    
}

package net.dzikoysk.funnyguilds.event.rank;

import net.dzikoysk.funnyguilds.basic.Rank;
import net.dzikoysk.funnyguilds.basic.User;

public class DeathsChangeEvent extends RankChangeEvent {

    public DeathsChangeEvent(EventCause eventCause, Rank rank, User doer, int change) {
        super(eventCause, rank, doer, change);
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Deaths change has been cancelled by the server!";
    }
    
}

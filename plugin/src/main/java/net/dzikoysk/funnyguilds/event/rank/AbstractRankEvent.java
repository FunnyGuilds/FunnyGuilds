package net.dzikoysk.funnyguilds.event.rank;

import net.dzikoysk.funnyguilds.event.FunnyEvent;
import net.dzikoysk.funnyguilds.user.User;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;

public abstract class AbstractRankEvent extends FunnyEvent {

    private final Option<User> affected;

    public AbstractRankEvent(EventCause eventCause, @Nullable User doer, @Nullable User affected) {
        super(eventCause, doer);
        this.affected = Option.of(affected);
    }

    //TODO: javadocs
    public Option<User> getAffected() {
        return this.affected;
    }

}

package net.dzikoysk.funnyguilds.feature.invitation;

import java.util.UUID;

public abstract class Invitation<F, T> {

    protected final UUID from;
    protected final UUID to;

    protected Invitation(UUID from, UUID to) {
        this.from = from;
        this.to = to;
    }

    public UUID getFrom() {
        return from;
    }

    public UUID getTo() {
        return to;
    }

}

package net.dzikoysk.funnyguilds.feature.invitation;

import java.util.Objects;
import java.util.UUID;

public abstract class Invitation<F, T> {

    protected final F from;
    protected final T to;

    protected Invitation(F from, T to) {
        this.from = from;
        this.to = to;
    }

    public F getFrom() {
        return this.from;
    }

    public T getTo() {
        return this.to;
    }

    public abstract UUID getFromUUID();

    public abstract UUID getToUUID();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Invitation)) {
            return false;
        }

        Invitation<?, ?> that = (Invitation<?, ?>) o;
        return this.from.equals(that.from) && this.to.equals(that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.from, this.to);
    }

}

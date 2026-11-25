package net.dzikoysk.funnyguilds.data;

import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class AbstractMutableEntity implements MutableEntity {

    private long version = 1;
    private long savedVersion;
    private boolean removed;

    @Override
    public synchronized void markChanged() {
        this.version++;
    }

    @Override
    public synchronized void markUnchanged() {
        this.savedVersion = this.version;
    }

    @Override
    public synchronized boolean wasChanged() {
        return this.version != this.savedVersion;
    }

    synchronized void markRemoved() {
        this.removed = true;
    }

    public synchronized boolean isRemoved() {
        return this.removed;
    }

    // The storage model orders writes. Only snapshot capture and acknowledgement need the entity lock.
    final synchronized <T> BooleanSupplier prepareSave(Supplier<T> capture, Predicate<T> write) {
        Snapshot<T> snapshot = new Snapshot<>(this.version, capture.get());
        return () -> {
            if (!write.test(snapshot.data())) {
                return false;
            }
            synchronized (this) {
                this.savedVersion = snapshot.version();
            }
            return true;
        };
    }

    private record Snapshot<T>(long version, T data) { }

}

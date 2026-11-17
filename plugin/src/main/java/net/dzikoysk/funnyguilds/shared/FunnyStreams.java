package net.dzikoysk.funnyguilds.shared;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Bridges the old expressible {@code panda.std.stream.PandaStream} API to
 * {@link Stream}. These helpers ease migration away from expressible.
 */
public final class FunnyStreams {

    private FunnyStreams() {
    }

    /**
     * Creates a sequential {@link Stream} over the elements of the given iterable.
     *
     * @param iterable the iterable to stream over, must not be null
     * @return a sequential stream over the iterable's elements
     */
    public static <T> Stream<T> of(Iterable<T> iterable) {
        if (iterable == null) {
            throw new NullPointerException("iterable must not be null");
        }

        return StreamSupport.stream(iterable.spliterator(), false);
    }

}

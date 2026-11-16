package net.dzikoysk.funnyguilds.shared;

import java.util.Optional;
import java.util.function.Supplier;
import panda.std.function.ThrowingSupplier;

/**
 * Bridges the old expressible {@code panda.std.Option} API to {@link Optional}.
 * These helpers mirror the semantics of the corresponding {@code Option} methods
 * so migration away from expressible stays behavior-preserving.
 */
public final class Optionals {

    private Optionals() {
    }

    /**
     * Invokes the given supplier and wraps its result in an {@link Optional},
     * treating any {@link Throwable} assignable to {@code exceptionType} as an
     * empty result.
     *
     * <p>Note: unlike expressible's {@code Option.attempt}, which wraps unexpected
     * failures in {@code AttemptFailedException}, checked exceptions other than
     * {@code exceptionType} are rethrown wrapped in a {@link RuntimeException},
     * while {@link RuntimeException} and {@link Error} instances are rethrown as-is
     * (unless they match {@code exceptionType}, in which case an empty result is
     * returned). A {@code null} result is still returned as {@link Optional#empty()}.
     */
    public static <T, E extends Throwable> Optional<T> attempt(Class<E> exceptionType, ThrowingSupplier<T, E> supplier) {
        try {
            return Optional.ofNullable(supplier.get());
        }
        catch (RuntimeException | Error throwable) {
            if (exceptionType.isInstance(throwable)) {
                return Optional.empty();
            }

            throw throwable;
        }
        catch (Throwable throwable) {
            if (exceptionType.isInstance(throwable)) {
                return Optional.empty();
            }

            throw new RuntimeException(throwable);
        }
    }

    /**
     * Invokes the given supplier only when the condition is true and wraps its
     * (possibly null) result in an {@link Optional}. Mirrors the null-tolerant
     * {@code Option.when(boolean, Supplier)}.
     */
    public static <T> Optional<T> when(boolean condition, Supplier<T> valueSupplier) {
        return condition ? Optional.ofNullable(valueSupplier.get()) : Optional.empty();
    }

    /**
     * Wraps the given (possibly null) value in an {@link Optional} only when the
     * condition is true.
     */
    public static <T> Optional<T> when(boolean condition, T value) {
        return condition ? Optional.ofNullable(value) : Optional.empty();
    }

    /**
     * Applies a checked cast to the value held by the given {@link Optional},
     * yielding an empty {@link Optional} when it holds a different type.
     */
    public static <T, S> Optional<S> castIf(Optional<T> optional, Class<S> type) {
        return optional.filter(type::isInstance).map(type::cast);
    }

}

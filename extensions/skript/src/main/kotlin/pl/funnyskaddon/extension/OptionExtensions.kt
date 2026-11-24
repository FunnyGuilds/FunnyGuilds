package pl.funnyskaddon.extension

import java.util.Optional
import java.util.stream.Stream

/**
 * Compatibility helpers for the FunnyGuilds 5.x migration: the API switched from
 * panda.std.Option to java.util.Optional, and some panda-style chain methods have
 * no direct Optional counterpart.
 */
fun <T> Optional<T>.peek(consumer: (T) -> Unit): Optional<T> {
    ifPresent(consumer)
    return this
}

fun <T> Optional<T>.orNull(): T? = orElse(null)

fun <T> Optional<T>.toStream(): Stream<T> = stream()

/**
 * flatMap variant accepting a function that produces an Optional, for use on Streams.
 */
fun <T, R> Stream<T>.flatMapOption(function: (T) -> Optional<R>): Stream<R> {
    return flatMap { value -> function(value).map { Stream.of(it) }.orElseGet { Stream.empty() } }
}

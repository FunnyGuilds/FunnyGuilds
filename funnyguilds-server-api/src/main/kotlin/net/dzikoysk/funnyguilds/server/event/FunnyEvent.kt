package net.dzikoysk.funnyguilds.server.event

interface FunnyEvent

interface CancellableEvent : FunnyEvent {
    var cancelled: Boolean
}

enum class FunnyEventPriority {
    LOWEST,
    LOW,
    NORMAL,
    HIGH,
    HIGHEST,
    MONITOR
}

fun interface FunnyEventListener<EVENT : FunnyEvent> {
    fun onEvent(event: EVENT)
}
package net.dzikoysk.funnyguilds.server

import net.dzikoysk.funnyguilds.server.entity.FunnyPlayer
import net.dzikoysk.funnyguilds.server.event.FunnyEvent
import net.dzikoysk.funnyguilds.server.event.FunnyEventListener
import net.dzikoysk.funnyguilds.server.event.FunnyEventPriority
import kotlin.reflect.KClass

class FakeServerContext : ServerContext {

    private val players = mutableSetOf<FunnyPlayer>()

    private data class FakeListener(val eventType: KClass<out FunnyEvent>, val priority: FunnyEventPriority, val listener: FunnyEventListener<*>)
    private val listeners = mutableSetOf<FakeListener>()

    override fun getOnlinePlayers(): Collection<FunnyPlayer> =
        players

    override fun <EVENT : FunnyEvent> registerListener(eventType: KClass<EVENT>, priority: FunnyEventPriority, listener: (EVENT) -> Unit) {
        listeners.add(FakeListener(eventType, priority, FunnyEventListener(listener)))
    }

    @Suppress("UNCHECKED_CAST")
    override fun <EVENT : FunnyEvent> callEvent(event: EVENT) {
        listeners
            .filter { it.eventType.isInstance(event) }
            .forEach { (it.listener as FunnyEventListener<EVENT>).onEvent(event) }
    }

}
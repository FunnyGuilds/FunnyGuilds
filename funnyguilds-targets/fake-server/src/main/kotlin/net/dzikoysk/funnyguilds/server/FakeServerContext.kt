package net.dzikoysk.funnyguilds.server

import net.dzikoysk.funnyguilds.server.command.FakeCommandContext
import net.dzikoysk.funnyguilds.server.command.FunnyCommand
import net.dzikoysk.funnyguilds.server.command.FunnyCommandEntry
import net.dzikoysk.funnyguilds.server.entity.FunnyPlayer
import net.dzikoysk.funnyguilds.server.event.FunnyEvent
import net.dzikoysk.funnyguilds.server.event.FunnyEventListener
import net.dzikoysk.funnyguilds.server.event.FunnyEventPriority
import kotlin.reflect.KClass

class FakeServerContext : ServerContext {

    private val players = mutableSetOf<FunnyPlayer>()

    override fun getOnlinePlayers(): Collection<FunnyPlayer> =
        players


    private data class FakeListener(val eventType: KClass<out FunnyEvent>, val priority: FunnyEventPriority, val listener: FunnyEventListener<*>)
    private val listeners = mutableSetOf<FakeListener>()

    override fun <EVENT : FunnyEvent> registerListener(eventType: KClass<EVENT>, priority: FunnyEventPriority, listener: (EVENT) -> Unit) {
        listeners.add(FakeListener(eventType, priority, FunnyEventListener(listener)))
    }

    @Suppress("UNCHECKED_CAST")
    override fun <EVENT : FunnyEvent> callEvent(event: EVENT) {
        listeners
            .filter { it.eventType.isInstance(event) }
            .forEach { (it.listener as FunnyEventListener<EVENT>).onEvent(event) }
    }

    private val commands = mutableSetOf<FunnyCommandEntry>()

    override fun registerCommand(pattern: String, permission: String, command: FunnyCommand) {
        commands.add(FunnyCommandEntry(pattern, permission, command))
    }

    fun callCommand(user: FunnyPlayer, command: String): FakeCommandContext {
        val args = command.split(" ")
        val commandName = args[0]
        val commandArgs = args.drop(1)
        val commandEntry = commands.first { it.pattern.startsWith(commandName) }

        val context = FakeCommandContext(
            commandPattern = commandEntry.pattern,
            caller = user,
            args = commandArgs
        )

        commandEntry.command.execute(context)
        return context
    }

}
package net.dzikoysk.funnyguilds.server.spigot

import net.dzikoysk.funnyguilds.server.ServerContext
import net.dzikoysk.funnyguilds.server.command.FunnyCommand
import net.dzikoysk.funnyguilds.server.entity.FunnyPlayer
import net.dzikoysk.funnyguilds.server.event.FunnyEvent
import net.dzikoysk.funnyguilds.server.event.FunnyEventPriority
import net.dzikoysk.funnyguilds.server.event.FunnyJoinEvent
import net.dzikoysk.funnyguilds.server.event.StandardizedEvent
import net.dzikoysk.funnyguilds.server.event.StandardizedEvent.PLAYER_JOIN
import net.dzikoysk.funnyguilds.server.spigot.command.SpigotCommandContext
import net.dzikoysk.funnyguilds.server.spigot.entity.SpigotPlayer
import org.bukkit.Server
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin
import kotlin.reflect.KClass
import kotlin.reflect.full.functions

class SpigotServerContext(private val plugin: Plugin, private val server: Server) : ServerContext {

    override fun getOnlinePlayers(): Collection<FunnyPlayer> =
        server.onlinePlayers.map { SpigotPlayer(it) }

    /* Events PoC */

    private fun FunnyEventPriority.toPriority(): EventPriority =
        EventPriority.valueOf(this.name)

    @Suppress("UNCHECKED_CAST")
    override fun <EVENT : FunnyEvent> registerListener(eventType: KClass<EVENT>, priority: FunnyEventPriority, listener: (EVENT) -> Unit) {
        when (StandardizedEvent.values().first()) {
            PLAYER_JOIN -> registerEvent(PlayerJoinEvent::class, priority) { listener(FunnyJoinEvent(SpigotPlayer(it.player)) as EVENT) }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <EVENT : Event> registerEvent(type: KClass<EVENT>, priority: FunnyEventPriority, listener: (EVENT) -> Unit) {
        server.pluginManager.registerEvent(
            type.java,
            object : Listener {},
            priority.toPriority(),
            { _, event -> listener(event as EVENT) },
            plugin
        )
    }

    @Suppress("UNREACHABLE_CODE", "UNUSED_EXPRESSION")
    override fun <EVENT : FunnyEvent> callEvent(event: EVENT) {
        val spigotEvent = when (event) {
            /* Map custom FunnyGuilds events into Spigot events */
            else -> throw IllegalArgumentException("Unsupported event type: ${event.javaClass}")
        }

        server.pluginManager.callEvent(spigotEvent)
    }

    // TODO: Replace with library (XD)
    override fun registerCommand(pattern: String, permission: String, command: FunnyCommand) {
        val simpleName = pattern.substringBefore(" ")

        server::class.functions
            .find { it.name == "getCommandMap" }
            ?.call(server)
            ?.let { commandMap ->
                commandMap::class.functions
                    .find { it.name == "register" }
                    ?.call(
                        commandMap,
                        simpleName,
                        object : BukkitCommand(pattern) {

                            init {
                                setPermission(permission)
                            }

                            override fun execute(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
                                command.execute(
                                    SpigotCommandContext(
                                        sender = sender,
                                        args = args.toList(),
                                        commandPattern = pattern
                                    )
                                )

                                return true
                            }

                        }
                    )
        }
    }

}
package net.dzikoysk.funnyguilds.server.spigot.command

import net.dzikoysk.funnyguilds.server.command.FunnyCommandContext
import net.dzikoysk.funnyguilds.server.entity.FunnyPlayer
import net.dzikoysk.funnyguilds.server.spigot.entity.SpigotPlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SpigotCommandContext(
    private val sender: CommandSender,
    override val args: List<String>,
    override val commandPattern: String
) : FunnyCommandContext {

    override val caller: FunnyPlayer? =
        (sender as? Player)?.let { SpigotPlayer(it) }

    override fun sendMessage(message: String) {
        sender.sendMessage(message)
    }

}
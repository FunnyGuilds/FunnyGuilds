package net.dzikoysk.funnyguilds.server.spigot.entity

import net.dzikoysk.funnyguilds.server.entity.FunnyPlayer
import org.bukkit.entity.Player
import java.util.UUID

class SpigotPlayer(val player: Player) : FunnyPlayer {

    override val uniqueId: UUID = player.uniqueId
    override val name: String = player.name

    override fun sendMessage(message: String) {
        player.sendMessage(message)
    }

    override fun hasPermissions(permission: String): Boolean =
        player.hasPermission(permission)

}
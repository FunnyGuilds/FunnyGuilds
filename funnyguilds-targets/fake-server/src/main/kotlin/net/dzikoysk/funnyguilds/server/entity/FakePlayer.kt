package net.dzikoysk.funnyguilds.server.entity

import java.util.UUID

data class FakePlayer(
    override val name: String = "FakePlayer",
    override val uniqueId: UUID = UUID.randomUUID(),
    val permissions: MutableSet<String> = mutableSetOf()
) : FunnyPlayer {

    val messages = mutableListOf<String>()

    override fun sendMessage(message: String) {
        messages.add(message)
    }

    override fun hasPermissions(permission: String): Boolean =
        permissions.contains(permission)

}
package net.dzikoysk.funnyguilds.server.entity

import java.util.UUID

interface FunnyPlayer {

    val uniqueId: UUID
    val name: String

    fun sendMessage(message: String)

    fun hasPermissions(permission: String): Boolean

}
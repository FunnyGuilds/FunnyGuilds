package net.dzikoysk.funnyguilds.server.command

import net.dzikoysk.funnyguilds.server.entity.FunnyPlayer

class FakeCommandContext(
    override val commandPattern: String,
    override val caller: FunnyPlayer?,
    override val args: List<String>
) : FunnyCommandContext {

    val messages = mutableListOf<String>()

    override fun sendMessage(message: String) {
        messages.add(message)
    }

}
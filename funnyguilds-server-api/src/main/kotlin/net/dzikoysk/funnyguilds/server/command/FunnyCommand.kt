package net.dzikoysk.funnyguilds.server.command

import net.dzikoysk.funnyguilds.server.entity.FunnyPlayer

interface FunnyCommandContext {

    val commandPattern: String
    val caller: FunnyPlayer?
    val args: List<String>

    fun sendMessage(message: String)

    fun getArgument(name: String): String =
        commandPattern.split(" ")
            .mapIndexed { index, part -> index to part }
            .filter { it.second.startsWith("{") && it.second.endsWith("}") }
            .firstOrNull { it.second.substring(1, it.second.length - 1) == name }
            ?.let { (index) -> args[index - 1] }
            ?: throw IllegalArgumentException("Argument $name not found!")

}

fun interface FunnyCommand {
    fun execute(context: FunnyCommandContext)
}

data class FunnyCommandEntry(
    val pattern: String,
    val permission: String,
    val command: FunnyCommand
) {

    constructor(
        pattern: String,
        permission: String,
        command: FunnyCommandContext.() -> Unit
    ) : this(
        pattern,
        permission,
        { ctx -> command(ctx) }
    )

}

fun interface FunnyCommandFactory {
    fun create(): FunnyCommandEntry
}
package net.dzikoysk.funnyguilds

import net.dzikoysk.funnyguilds.server.ServerContext

data class InitContext(
    var definitions: MutableList<Any> = mutableListOf()
)

interface FunnyComponent

interface FunnyModule {

    fun onLoad(initContext: InitContext) {}

    fun onEnable(context: ServerContext, funnyGuilds: FunnyGuilds) {}

    fun onDisable(context: ServerContext, funnyGuilds: FunnyGuilds) {}

}
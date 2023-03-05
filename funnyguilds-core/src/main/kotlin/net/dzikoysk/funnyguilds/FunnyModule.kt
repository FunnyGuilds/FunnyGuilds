package net.dzikoysk.funnyguilds

import net.dzikoysk.funnyguilds.server.ServerContext

data class InitContext(
    var definitions: MutableList<Any> = mutableListOf()
)

interface FunnyComponent

interface FunnyModule {

    /** Initialize internal components & register core functionalities through init context */
    fun onLoad(initContext: InitContext) {}

    /** Register listeners, commands, etc. / use dependencies */
    fun onEnable(context: ServerContext, funnyGuilds: FunnyGuilds) {}

    /** Called when plugin is being disabled/reloaded */
    fun onDisable(context: ServerContext, funnyGuilds: FunnyGuilds) {}

}
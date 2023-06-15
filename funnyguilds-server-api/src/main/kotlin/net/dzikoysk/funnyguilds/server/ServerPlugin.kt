package net.dzikoysk.funnyguilds.server

interface ServerPlugin {

    fun onLoad(context: ServerContext)

    fun onEnable(context: ServerContext)

    fun onDisable(context: ServerContext)

}
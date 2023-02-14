package net.dzikoysk.funnyguilds

import com.dzikoysk.sqiffy.Sqiffy
import net.dzikoysk.funnyguilds.server.ServerContext
import net.dzikoysk.funnyguilds.server.ServerPlugin
import kotlin.reflect.KClass

class FunnyGuilds(
    val sqiffy: Sqiffy,
    val modules: List<FunnyModule> = emptyList()
) {

    private val components: MutableMap<KClass<out FunnyComponent>, FunnyComponent> = mutableMapOf()

    fun registerComponent(component: FunnyComponent) {
        require(component::class !in components.keys) { "Component ${component::class} is already registered!" }
        components[component::class] = component
    }

    @Suppress("UNCHECKED_CAST")
    fun <C : FunnyComponent> getComponent(componentClass: KClass<C>): C =
        components[componentClass] as? C? ?: throw IllegalStateException("Component $componentClass is not registered!")

    inline fun <reified T : FunnyComponent> getComponent(): T =
        getComponent(T::class)

    private val serverPlugin = object : ServerPlugin {

        override fun onLoad(context: ServerContext) {
            val initContext = InitContext()

            modules.forEach {
                it.onLoad(initContext)
            }

            sqiffy.transaction {
                val changeLog = sqiffy.generateChangeLog(*initContext.definitions.map { it::class }.toTypedArray())
                sqiffy.runMigrations(changeLog)
            }
        }

        override fun onEnable(context: ServerContext) {
            modules.forEach {
                it.onEnable(context, this@FunnyGuilds)
            }
        }

        override fun onDisable(context: ServerContext) {
            modules.forEach {
                it.onDisable(context, this@FunnyGuilds)
            }
        }

    }

    fun getServerPlugin(): ServerPlugin =
        serverPlugin

}
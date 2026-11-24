package pl.funnyskaddon

import ch.njol.skript.Skript
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.config.PluginConfiguration
import org.skriptlang.skript.addon.SkriptAddon
import pl.funnyskaddon.skript.registration.ClassExplorer

/**
 * Entry point of the bundled Skript extension. Invoked by FunnyGuilds
 * through reflection when the Skript plugin is present, so the core
 * plugin keeps no compile-time dependency on this module.
 */
object FunnySkAddon {

    lateinit var addon: SkriptAddon
    lateinit var fgConfiguration: PluginConfiguration

    fun register(funnyGuilds: FunnyGuilds) {
        fgConfiguration = funnyGuilds.pluginConfiguration

        addon = Skript.instance().registerAddon(FunnyGuilds::class.java, "FunnySkAddon")
        ClassExplorer.loadClasses(
            funnyGuilds.javaClass.classLoader,
            "pl.funnyskaddon.skript",
            "condition.guild",
            "condition.player",
            "effect.guild",
            "effect.player",
            "event.guild",
            "event.guild.ally",
            "event.guild.member",
            "event.rank",
            "expression.event",
            "expression.guild",
            "expression.player",
            "expression.top",
            "expression.config",
            "type"
        )
    }

}

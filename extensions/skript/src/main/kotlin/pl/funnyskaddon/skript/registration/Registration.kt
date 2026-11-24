package pl.funnyskaddon.skript.registration

import ch.njol.skript.lang.Condition
import ch.njol.skript.lang.Effect
import ch.njol.skript.lang.Expression as SkriptExpression
import ch.njol.skript.lang.util.SimpleEvent
import ch.njol.skript.expressions.base.PropertyExpression
import org.bukkit.event.Event
import org.skriptlang.skript.bukkit.registration.BukkitSyntaxInfos
import org.skriptlang.skript.registration.DefaultSyntaxInfos
import org.skriptlang.skript.registration.SyntaxInfo
import org.skriptlang.skript.registration.SyntaxRegistry
import org.skriptlang.skript.util.Priority
import pl.funnyskaddon.FunnySkAddon

/**
 * Thin Kotlin helpers over the modern (2.14+) syntax registry API.
 * Documentation is read from ch.njol.skript.doc annotations on the element classes
 * by Skript's own docs generator, so no extra registration data is needed here.
 */
object Registration {

    /**
     * Expands a guild-source core into the six standard guild accessor patterns,
     * preserving the conventional pattern order (guild, string, offlineplayer, location, block, object).
     * The core is the text following "%guild%", including any leading "['s]".
     */
    fun guildSources(core: String): Array<String> {
        val bare = core.removePrefix("['s]")
        return arrayOf(
            "%guild%$core",
            "%string%['s] [guild]$bare",
            "%offlineplayer%['s] guild$core",
            "%location%['s] guild$core",
            "%block%['s] guild$core",
            "%object%['s] guild$core"
        )
    }

    @PublishedApi
    internal val registry: SyntaxRegistry
        get() = FunnySkAddon.addon.syntaxRegistry()

    // Modern replacements for the deprecated ExpressionType enum
    val SIMPLE: Priority = SyntaxInfo.SIMPLE
    val PROPERTY: Priority = PropertyExpression.DEFAULT_PRIORITY
    val COMBINED: Priority = SyntaxInfo.COMBINED
    val PATTERN_MATCHES_EVERYTHING: Priority = SyntaxInfo.PATTERN_MATCHES_EVERYTHING

    inline fun <reified E : Effect> registerEffect(vararg patterns: String) {
        registry.register(
            SyntaxRegistry.EFFECT,
            SyntaxInfo.builder(E::class.java)
                .addPatterns(*patterns)
                .build()
        )
    }

    inline fun <reified E : Condition> registerCondition(vararg patterns: String) {
        registry.register(
            SyntaxRegistry.CONDITION,
            SyntaxInfo.builder(E::class.java)
                .addPatterns(*patterns)
                .build()
        )
    }

    inline fun <reified E : SkriptExpression<R>, reified R : Any> registerExpression(
        priority: Priority,
        vararg patterns: String
    ) {
        registry.register(
            SyntaxRegistry.EXPRESSION,
            DefaultSyntaxInfos.Expression.builder<E, R>(
                    E::class.java,
                    R::class.java
                )
                .priority(priority)
                .addPatterns(*patterns)
                .build()
        )
    }

    fun <E : SimpleEvent> registerEvent(
        eventClass: Class<E>,
        bukkitEventClass: Class<out Event>,
        name: String,
        vararg patterns: String
    ) {
        registry.register(
            BukkitSyntaxInfos.Event.KEY,
            BukkitSyntaxInfos.Event.builder(eventClass, name)
                .addPatterns(*patterns)
                .addEvent(bukkitEventClass)
                .build()
        )
    }

}

package pl.funnyskaddon.skript.expression.event

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Events
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import pl.funnyskaddon.skript.expression.FunnyExpression
import pl.funnyskaddon.skript.registration.Registration.PROPERTY
import pl.funnyskaddon.skript.registration.Registration.SIMPLE
import pl.funnyskaddon.skript.registration.Registration.registerExpression
import ch.njol.util.Kleenean
import net.dzikoysk.funnyguilds.event.guild.GuildBanEvent
import net.dzikoysk.funnyguilds.event.guild.GuildExtendValidityEvent
import org.bukkit.event.Event
import java.time.Duration

@Name("Time")
@Description("Returns the time")
@Since("2.0.0")
@Events(
    "guild ban",
    "guild extend validity"
)
class EventTimeExpression : FunnyExpression<Long>(Long::class.javaObjectType) {

    companion object {
        init {
            registerExpression<EventTimeExpression, Long>(SIMPLE, *EventType.patterns.toTypedArray())
        }
    }

    private enum class EventType(var pattern: String, vararg var events: Class<out Event>) {

        BAN("time", GuildBanEvent::class.java) {
            override fun get(event: Event): Duration? {
                if (event is GuildBanEvent) {
                    return event.time
                }
                return null
            }
        },

        EXTEND_VALIDITY("[extend( |-)]time", GuildExtendValidityEvent::class.java) {
            override fun get(event: Event): Duration? {
                if (event is GuildExtendValidityEvent) {
                    return event.extendTime
                }
                return null
            }
        };

        init {
            this.pattern = "[the] $pattern"
        }

        companion object {
            val patterns = mutableSetOf<String>()

            init {
                for (value in values()) {
                    patterns.add(value.pattern)
                }
            }
        }

        abstract operator fun get(event: Event): Duration?
    }

    private lateinit var type: EventType

    override fun init(
        expression: Array<Expression<*>>,
        matchedPattern: Int,
        isDelayed: Kleenean,
        parseResult: SkriptParser.ParseResult
    ): Boolean {
        type = EventType.values()[matchedPattern]
        if (!EventExpressionUtil.isCurrentEvent(*type.events)) {
            error(
                "The '" + type.pattern + "' can only be used in a " + type.name + " event"
)
            return false
        }
        return true
    }

    override fun get(event: Event): Array<Long?>? {
        for (classEvent in type.events) {
            if (classEvent.isInstance(event)) {
                return arrayOf(type[event]?.toMillis() ?: 0L)
            }
        }
        return null
    }

    override fun isSingle(): Boolean {
        return true
    }



}
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
import net.dzikoysk.funnyguilds.event.rank.*
import org.bukkit.event.Event

@Name("Change")
@Description("Returns the ranking change")
@Since("2.0.0")
@Events(
    "kills change",
    "deaths change",
    "assists change",
    "logouts change",
    "points change"
)
class EventChangeExpression : FunnyExpression<Int>(Int::class.javaObjectType) {

    companion object {
        init {
            registerExpression<EventChangeExpression, Int>(SIMPLE, *EventType.patterns.toTypedArray())
        }
    }

    private enum class EventType(var pattern: String, vararg var events: Class<out Event>) {

        KILLS_CHANGE("[rank|ranking] [kills( |-)]change", KillsChangeEvent::class.java) {
            override fun get(event: Event): Int? {
                if (event is KillsChangeEvent) {
                    return event.killsChange
                }
                return null
            }
        },

        DEATHS_CHANGE("[rank|ranking] [deaths( |-)]change", DeathsChangeEvent::class.java) {
            override fun get(event: Event): Int? {
                if (event is DeathsChangeEvent) {
                    return event.deathsChange
                }
                return null
            }
        },

        ASSISTS_CHANGE("[rank|ranking] [assists( |-)]change", AssistsChangeEvent::class.java) {
            override fun get(event: Event): Int? {
                if (event is AssistsChangeEvent) {
                    return event.assistsChange
                }
                return null
            }
        },

        LOGOUTS_CHANGE("[rank|ranking] [logouts( |-)]change", LogoutsChangeEvent::class.java) {
            override fun get(event: Event): Int? {
                if (event is LogoutsChangeEvent) {
                    return event.logoutsChange
                }
                return null
            }
        },

        POINTS_CHANGE("[rank|ranking] [points( |-)]change", PointsChangeEvent::class.java) {
            override fun get(event: Event): Int? {
                if (event is PointsChangeEvent) {
                    return event.pointsChange
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

        abstract operator fun get(event: Event): Int?
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

    override fun get(event: Event): Array<Int?>? {
        for (classEvent in type.events) {
            if (classEvent.isInstance(event)) {
                return arrayOf(type[event])
            }
        }
        return null
    }

    override fun isSingle(): Boolean {
        return true
    }



}
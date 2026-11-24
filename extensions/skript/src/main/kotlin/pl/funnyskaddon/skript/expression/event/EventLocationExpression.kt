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
import net.dzikoysk.funnyguilds.event.guild.GuildBaseChangeEvent
import net.dzikoysk.funnyguilds.event.guild.GuildMoveEvent
import org.bukkit.Location
import org.bukkit.event.Event

@Name("Location")
@Description("Returns the location")
@Since("2.0.0")
@Events(
    "guild move",
    "guild base change"
)
class EventLocationExpression : FunnyExpression<Location>(Location::class.java) {

    companion object {
        init {
            registerExpression<EventLocationExpression, Location>(SIMPLE, *EventType.patterns.toTypedArray())
        }
    }

    private enum class EventType(var pattern: String, vararg var events: Class<out Event>) {

        MOVE("[new( |-)]location", GuildMoveEvent::class.java) {
            override fun get(event: Event): Location? {
                if (event is GuildMoveEvent) {
                    return event.newLocation
                }
                return null
            }
        },

        BASE_CHANGE("[new( |-)][base( |-)]]location", GuildBaseChangeEvent::class.java) {
            override fun get(event: Event): Location? {
                if (event is GuildBaseChangeEvent) {
                    return event.newBaseLocation
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

        abstract operator fun get(event: Event): Location?
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

    override fun get(event: Event): Array<Location?>? {
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
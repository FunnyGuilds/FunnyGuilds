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
import net.dzikoysk.funnyguilds.event.FunnyEvent
import org.bukkit.event.Event

@Name("Cause")
@Description("Returns the cause of the event")
@Since("2.0.0")
@Events(
    "guild delete",
    "guild delete",
    "guild ban",
    "guild unban",
    "guild move",
    "guild base change",
    "guild rename",
    "guild tag change",
    "guild enlarge",
    "guild extend validity",
    "guild lives change",
    "guild member invite",
    "guild member accept invite",
    "guild member revoke invite",
    "guild member join",
    "guild member leave",
    "guild member kick",
    "guild member leader",
    "guild member deputy",
    "guild send ally invitation",
    "guild accept ally invitation",
    "guild revoke ally invitation",
    "guild break ally",
    "guild region enter",
    "guild region leave",
    "kills change",
    "deaths change",
    "assists change",
    "logouts change",
    "points change"
)
class EventCauseExpression : FunnyExpression<FunnyEvent.EventCause>(FunnyEvent.EventCause::class.java) {

    companion object {
        init {
            registerExpression<EventCauseExpression, FunnyEvent.EventCause>(SIMPLE, *EventType.patterns.toTypedArray())
        }
    }

    private enum class EventType(var pattern: String, vararg var events: Class<out Event>) {

        FUNNY("cause", FunnyEvent::class.java) {
            override fun get(event: Event): FunnyEvent.EventCause? {
                if (event is FunnyEvent) {
                    return event.eventCause
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

        abstract operator fun get(event: Event): FunnyEvent.EventCause?
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

    override fun get(event: Event): Array<FunnyEvent.EventCause?>? {
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
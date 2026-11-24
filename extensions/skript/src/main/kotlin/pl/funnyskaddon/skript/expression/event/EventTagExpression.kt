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
import net.dzikoysk.funnyguilds.event.guild.GuildPreTagChangeEvent
import org.bukkit.event.Event

@Name("Tag")
@Description("Returns the guild tag before or after the change")
@Since("2.0.0")
@Events(
    "guild tag change"
)
class EventTagExpression : FunnyExpression<String>(String::class.java) {

    companion object {
        init {
            registerExpression<EventTagExpression, String>(SIMPLE, *EventType.patterns.toTypedArray())
        }
    }

    private enum class EventType(var pattern: String, vararg var events: Class<out Event>) {

        TAG_CHANGE_NEW("new( |-)tag", GuildPreTagChangeEvent::class.java) {
            override fun get(event: Event): String? {
                if (event is GuildPreTagChangeEvent) {
                    return event.newTag
                }
                return null
            }
        },

        TAG_CHANGE_OLD("old( |-)tag", GuildPreTagChangeEvent::class.java) {
            override fun get(event: Event): String? {
                if (event is GuildPreTagChangeEvent) {
                    return event.oldTag
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

        abstract operator fun get(event: Event): String?
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

    override fun get(event: Event): Array<String?>? {
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
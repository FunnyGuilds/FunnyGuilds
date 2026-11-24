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
import net.dzikoysk.funnyguilds.event.guild.GuildChatEvent
import net.dzikoysk.funnyguilds.event.guild.GuildPreChatEvent
import org.bukkit.event.Event

@Name("Chat Type")
@Description("Returns the guild chat message type: PRIVATE, ALLY, ALL")
@Since("2.0.0")
@Events(
    "guild pre chat event",
    "guild chat event"
)
class EventChatTypeExpression : FunnyExpression<GuildChatEvent.Type>(GuildChatEvent.Type::class.java) {

    companion object {
        init {
            registerExpression<EventChatTypeExpression, GuildChatEvent.Type>(SIMPLE, *EventType.patterns.toTypedArray())
        }
    }

    private enum class EventType(var pattern: String, vararg var events: Class<out Event>) {

        PRE_CHAT("[chat( |-)]type", GuildPreChatEvent::class.java) {
            override fun get(event: Event): GuildChatEvent.Type? {
                if (event is GuildPreChatEvent) {
                    return event.type
                }
                return null
            }
        },

        CHAT("[chat( |-)]type", GuildChatEvent::class.java) {
            override fun get(event: Event): GuildChatEvent.Type? {
                if (event is GuildChatEvent) {
                    return event.type
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

        abstract operator fun get(event: Event): GuildChatEvent.Type?
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

    override fun get(event: Event): Array<GuildChatEvent.Type?>? {
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
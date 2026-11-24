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
import net.dzikoysk.funnyguilds.guild.Guild
import org.bukkit.event.Event

@Name("Receivers")
@Description("Returns the guilds that received the guild chat message")
@Since("2.0.0")
@Events(
    "guild pre chat event",
    "guild chat event"
)
class EventReceiversExpression : FunnyExpression<Guild>(Guild::class.java) {

    companion object {
        init {
            registerExpression<EventReceiversExpression, Guild>(SIMPLE, *EventType.patterns.toTypedArray())
        }
    }

    private enum class EventType(var pattern: String, vararg var events: Class<out Event>) {

        PRE_CHAT("[message( |-)]receivers", GuildPreChatEvent::class.java) {
            override fun get(event: Event): Set<Guild>? {
                if (event is GuildPreChatEvent) {
                    return event.receivers
                }
                return null
            }
        },

        CHAT("[message( |-)]receivers", GuildChatEvent::class.java) {
            override fun get(event: Event): Set<Guild>? {
                if (event is GuildChatEvent) {
                    return event.receivers
                }
                return null
            }
        }, ;

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

        abstract operator fun get(event: Event): Set<Guild>?
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

    override fun get(event: Event): Array<Guild?>? {
        for (classEvent in type.events) {
            if (classEvent.isInstance(event)) {
                return type[event]?.toTypedArray()
            }
        }
        return null
    }

    override fun isSingle(): Boolean {
        return true
    }



}
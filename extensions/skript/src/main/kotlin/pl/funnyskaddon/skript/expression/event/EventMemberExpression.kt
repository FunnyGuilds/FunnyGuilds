package pl.funnyskaddon.skript.expression.event

import pl.funnyskaddon.extension.orNull

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
import net.dzikoysk.funnyguilds.event.guild.member.*
import org.bukkit.entity.Player
import org.bukkit.event.Event
import pl.funnyskaddon.extension.getPlayerOption

@Name("Member")
@Description("Returns the member involved in the event")
@Since("2.0.0")
@Events(
    "guild member join",
    "guild member leave",
    "guild member kick",
    "guild member leader",
    "guild member deputy",
)
class EventMemberExpression : FunnyExpression<Player>(Player::class.java) {

    companion object {
        init {
            registerExpression<EventMemberExpression, Player>(SIMPLE, *EventType.patterns.toTypedArray())
        }
    }

    private enum class EventType(var pattern: String, vararg var events: Class<out Event>) {

        MEMBER(
            "member",
            GuildMemberInviteEvent::class.java,
            GuildMemberAcceptInviteEvent::class.java,
            GuildMemberRevokeInviteEvent::class.java,
            GuildMemberJoinEvent::class.java,
            GuildMemberLeaveEvent::class.java,
            GuildMemberKickEvent::class.java,
            GuildMemberLeaderEvent::class.java,
            GuildMemberDeputyEvent::class.java
        ) {
            override fun get(event: Event): Player? {
                return when (event) {
                    is GuildMemberInviteEvent -> {
                        event.member.getPlayerOption().orNull()
                    }
                    is GuildMemberAcceptInviteEvent -> {
                        event.member.getPlayerOption().orNull()
                    }
                    is GuildMemberRevokeInviteEvent -> {
                        event.member.getPlayerOption().orNull()
                    }
                    is GuildMemberJoinEvent -> {
                        event.member.getPlayerOption().orNull()
                    }
                    is GuildMemberLeaveEvent -> {
                        event.member.getPlayerOption().orNull()
                    }
                    is GuildMemberKickEvent -> {
                        event.member.getPlayerOption().orNull()
                    }
                    is GuildMemberLeaderEvent -> {
                        event.member.getPlayerOption().orNull()
                    }
                    is GuildMemberDeputyEvent -> {
                        event.member.getPlayerOption().orNull()
                    }
                    else -> null
                }
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

        abstract operator fun get(event: Event): Player?
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

    override fun get(event: Event): Array<Player?>? {
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
package pl.funnyskaddon.skript.expression.event

import pl.funnyskaddon.extension.orNull

import ch.njol.skript.lang.parser.ParserInstance
import ch.njol.skript.registrations.EventValues
import ch.njol.util.coll.CollectionUtils
import net.dzikoysk.funnyguilds.event.FunnyEvent
import net.dzikoysk.funnyguilds.user.User
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.skriptlang.skript.lang.converter.Converter
import pl.funnyskaddon.extension.getPlayerOption

class EventExpressionUtil {

    companion object {

        init {
            EventValues.registerEventValue(
                FunnyEvent::class.java,
                Player::class.java,
                object : Converter<FunnyEvent, Player?> {
                    override fun convert(event: FunnyEvent): Player? {
                        if (event.doer == null) {
                            return null
                        }
                        return event.doer.flatMap(User::getPlayerOption).orNull()
                    }
                },
                0
            )
        }

        fun isCurrentEvent(vararg events: Class<out Event?>?): Boolean {
            return CollectionUtils.containsAnySuperclass(events, *ParserInstance.get().currentEvents)
        }

    }

}
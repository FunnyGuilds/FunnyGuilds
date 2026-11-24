package pl.funnyskaddon.skript.type

import ch.njol.skript.classes.ClassInfo
import ch.njol.skript.classes.Parser
import ch.njol.skript.classes.Serializer
import ch.njol.skript.expressions.base.EventValueExpression
import ch.njol.skript.lang.ParseContext
import ch.njol.skript.registrations.Classes
import ch.njol.yggdrasil.Fields
import net.dzikoysk.funnyguilds.event.FunnyEvent

class CauseType {

    companion object {
        init {
            Classes.registerClass(
                ClassInfo(FunnyEvent.EventCause::class.java, "cause")
                    .user("causes")
                    .name("cause")
                    .defaultExpression(EventValueExpression(FunnyEvent.EventCause::class.java))
                    .parser(object : Parser<FunnyEvent.EventCause>() {

                        override fun parse(input: String, context: ParseContext): FunnyEvent.EventCause? {
                            return null
                        }

                        override fun canParse(context: ParseContext): Boolean {
                            return false
                        }

                        override fun toVariableNameString(cause: FunnyEvent.EventCause): String {
                            return cause.toString()
                        }

                        override fun toString(cause: FunnyEvent.EventCause, flags: Int): String {
                            return toVariableNameString(cause)
                        }

                    })
                    .serializer(object : Serializer<FunnyEvent.EventCause>() {

                        override fun serialize(cause: FunnyEvent.EventCause): Fields {
                            val fields = Fields()
                            fields.putPrimitive("cause", cause.toString())
                            return fields
                        }

                        override fun deserialize(fields: Fields): FunnyEvent.EventCause {
                            return FunnyEvent.EventCause.valueOf(
                                fields.getPrimitive("cause", String::class.java).lowercase()
                            )
                        }

                        override fun deserialize(cause: FunnyEvent.EventCause, fields: Fields) {
                            assert(false)
                        }

                        override fun mustSyncDeserialization(): Boolean {
                            return false
                        }

                        override fun canBeInstantiated(): Boolean {
                            return false
                        }

                    })
            )
        }
    }

}
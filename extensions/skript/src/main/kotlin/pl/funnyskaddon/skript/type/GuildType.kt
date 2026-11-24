package pl.funnyskaddon.skript.type

import pl.funnyskaddon.extension.orNull

import ch.njol.skript.classes.ClassInfo
import ch.njol.skript.classes.Parser
import ch.njol.skript.classes.Serializer
import ch.njol.skript.expressions.base.EventValueExpression
import ch.njol.skript.lang.ParseContext
import ch.njol.skript.registrations.Classes
import ch.njol.yggdrasil.Fields
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.guild.Guild
import java.util.*

class GuildType {

    companion object {
        init {
            val funnyGuilds = FunnyGuilds.getInstance()

            Classes.registerClass(
                ClassInfo(Guild::class.java, "guild")
                    .user("guilds")
                    .name("guild")
                    .defaultExpression(EventValueExpression(Guild::class.java))
                    .parser(object : Parser<Guild>() {
                        override fun parse(input: String, context: ParseContext): Guild? {
                            return FunnyGuilds.getInstance().guildManager.findByName(input).orNull()
                        }

                        override fun canParse(context: ParseContext): Boolean {
                            return true
                        }

                        override fun toVariableNameString(guild: Guild): String {
                            return guild.name
                        }

                        override fun toString(guild: Guild, flags: Int): String {
                            return toVariableNameString(guild)
                        }
                    })
                    .serializer(object : Serializer<Guild>() {
                        override fun serialize(guild: Guild): Fields {
                            val fields = Fields()
                            fields.putPrimitive("uuid", guild.uuid)
                            return fields
                        }

                        override fun deserialize(fields: Fields): Guild? {
                            return FunnyGuilds.getInstance().guildManager.findByUuid(
                                fields.getPrimitive(
                                    "uuid",
                                    UUID::class.java
                                )
                            ).orNull()
                        }

                        override fun deserialize(guild: Guild, fields: Fields) {
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
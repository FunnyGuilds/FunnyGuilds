package pl.funnyskaddon.skript.effect.guild

import pl.funnyskaddon.extension.peek

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.event.FunnyEvent
import net.dzikoysk.funnyguilds.event.SimpleEventHandler
import net.dzikoysk.funnyguilds.event.guild.GuildPreTagChangeEvent
import net.dzikoysk.funnyguilds.event.guild.GuildTagChangeEvent
import org.bukkit.event.Event
import pl.funnyskaddon.skript.effect.GuildValueEffect
import pl.funnyskaddon.skript.getGuild
import pl.funnyskaddon.skript.getValue
import pl.funnyskaddon.skript.registration.Registration.registerEffect

@Name("Set Guild Tag")
@Description("Sets (changes) the guild's tag")
@Since("2.0.0")
@Examples(
    "set \"FajnaNazwa\" guild tag to \"AC4U\"",
)
class GuildSetTagEffect : GuildValueEffect<String>(false) {

    companion object {
        init {
            registerEffect<GuildSetTagEffect>(
                "set %guild%['s] tag to %string%",
                "set %string%['s] [guild] tag to %string%",
                "set %offlineplayer%['s] [guild] tag to %string%",
                "set %location%['s] [guild] tag to %string%",
                "set %block%['s] [guild] tag to %string%",
                "set %object%['s] [guild] tag to %string%"
            )
        }
    }

    override fun execute(event: Event) {
        event.getGuild(guildExpression)
            .peek { guild ->
                event.getValue(valueExpression)
                    .peek valuePeek@{ value ->
                        val oldTag = guild.tag

                        val guildPreTagChangeEvent =
                            GuildPreTagChangeEvent(FunnyEvent.EventCause.CONSOLE, null, guild, oldTag, value)
                        if (!SimpleEventHandler.handle(guildPreTagChangeEvent)) {
                            return@valuePeek
                        }

                        guild.tag = guildPreTagChangeEvent.newTag

                        SimpleEventHandler.handle(
                            GuildTagChangeEvent(
                                FunnyEvent.EventCause.CONSOLE,
                                null,
                                guild,
                                oldTag,
                                value
                            )
                        )
                    }
            }
    }

}
package pl.funnyskaddon.skript.effect.guild

import pl.funnyskaddon.extension.peek

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.event.FunnyEvent
import net.dzikoysk.funnyguilds.event.SimpleEventHandler
import net.dzikoysk.funnyguilds.event.guild.GuildLivesChangeEvent
import org.bukkit.event.Event
import pl.funnyskaddon.skript.effect.GuildValueEffect
import pl.funnyskaddon.skript.getGuild
import pl.funnyskaddon.skript.getValue
import pl.funnyskaddon.skript.registration.Registration.registerEffect

@Name("Set Guild Lives")
@Description("Sets the number of guild lives")
@Since("2.0.0")
@Examples(
    "set amount \"FajnaNazwa\" guild lives to 6",
)
class GuildSetLivesEffect : GuildValueEffect<Number>(false) {

    companion object {
        init {
            registerEffect<GuildSetLivesEffect>(
                "set [(number|amount) of] %guild%['s] lives to %number%",
                "set [(number|amount) of] %string%['s] [guild] lives to %number%",
                "set [(number|amount) of] %object%['s] [guild] lives to %number%",
                "set [(number|amount) of] %object%['s] [guild] lives to %number%"
            )
        }
    }

    override fun execute(event: Event) {
        event.getGuild(guildExpression)
            .peek { guild ->
                event.getValue(valueExpression)
                    .map(Number::toInt)
                    .peek valuePeek@{ value ->
                        val livesChangeEvent = GuildLivesChangeEvent(FunnyEvent.EventCause.CONSOLE, null, guild, value)
                        if (!SimpleEventHandler.handle(livesChangeEvent)) {
                            return@valuePeek
                        }

                        guild.lives = livesChangeEvent.newLives
                    }
            }
    }

}
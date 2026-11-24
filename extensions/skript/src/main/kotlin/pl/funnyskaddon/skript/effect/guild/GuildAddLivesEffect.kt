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

@Name("Add Guild Lives")
@Description("Adds lives to the guild")
@Since("2.0.0")
@Examples(
    "add 4 lives to \"AC4U\" guild",
)
class GuildAddLivesEffect : GuildValueEffect<Number>(false) {

    companion object {
        init {
            registerEffect<GuildAddLivesEffect>(
                "add %number% live[s] to %guild%",
                "add %number% live[s] to %string% [guild]",
                "add %number% live[s] to %offlineplayer%['s] guild",
                "add %number% live[s] to %location%['s] guild",
                "add %number% live[s] to %block%['s] guild",
                "add %number% live[s] to %object%['s] guild"
            )
        }
    }

    override fun execute(event: Event) {
        event.getGuild(guildExpression)
            .peek { guild ->
                event.getValue(valueExpression)
                    .map(Number::toInt)
                    .peek valuePeek@{ value ->
                        val livesChangeEvent =
                            GuildLivesChangeEvent(FunnyEvent.EventCause.CONSOLE, null, guild, guild.lives + value)
                        if (!SimpleEventHandler.handle(livesChangeEvent)) {
                            return@valuePeek
                        }

                        guild.lives = livesChangeEvent.newLives
                    }
            }
    }

}
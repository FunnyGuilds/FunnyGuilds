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


@Name("Remove Guild Lives")
@Description("Removes lives from the guild")
@Since("2.0.0")
@Examples(
    "remove 4 lives from \"AC4U\" guild",
)
class GuildRemoveLivesEffect : GuildValueEffect<Number>(false) {

    companion object {
        init {
            registerEffect<GuildRemoveLivesEffect>(
                "remove %number% live[s] from %guild%",
                "remove %number% live[s] from %string%['s] [guild]",
                "remove %number% live[s] from %offlineplayer%['s] [guild]",
                "remove %number% live[s] from %location%['s] [guild]",
                "remove %number% live[s] from %block%['s] [guild]",
                "remove %number% live[s] from %object%['s] [guild]"
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
                            GuildLivesChangeEvent(FunnyEvent.EventCause.CONSOLE, null, guild, guild.lives - value)
                        if (!SimpleEventHandler.handle(livesChangeEvent)) {
                            return@valuePeek
                        }

                        guild.lives = livesChangeEvent.newLives
                    }
            }
    }

}
package pl.funnyskaddon.skript.effect.guild

import pl.funnyskaddon.extension.peek

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.event.FunnyEvent
import net.dzikoysk.funnyguilds.event.SimpleEventHandler
import net.dzikoysk.funnyguilds.event.guild.GuildDeleteEvent
import org.bukkit.event.Event
import pl.funnyskaddon.skript.effect.GuildEffect
import pl.funnyskaddon.skript.getGuild
import pl.funnyskaddon.skript.registration.Registration.registerEffect


@Name("Delete Guild")
@Description("Deletes the guild")
@Since("2.0.0")
@Examples(
    "delete \"AC4U\" guild",
)
class GuildDeleteEffect : GuildEffect() {

    companion object {
        init {
            registerEffect<GuildDeleteEffect>(
                "delete %guild%",
                "delete %string% [guild]",
                "delete %offlineplayer%['s] [guild]",
                "delete %location%['s] [guild]",
                "delete %block%['s] [guild]",
                "delete %object%['s] [guild]"
            )
        }
    }

    override fun execute(event: Event) {
        event.getGuild(guildExpression)
            .peek { guild ->
                if (!SimpleEventHandler.handle(
                        GuildDeleteEvent(
                            FunnyEvent.EventCause.CONSOLE,
                            null,
                            guild
                        )
                    )
                ) {
                    return@peek
                }

                FunnyGuilds.getInstance().guildManager.deleteGuild(FunnyGuilds.getInstance(), guild)
            }
    }

}
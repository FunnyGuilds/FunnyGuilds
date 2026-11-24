package pl.funnyskaddon.skript.effect.guild

import pl.funnyskaddon.extension.peek

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.util.Timespan
import ch.njol.skript.util.Timespan.TimePeriod
import net.dzikoysk.funnyguilds.event.FunnyEvent
import net.dzikoysk.funnyguilds.event.SimpleEventHandler
import net.dzikoysk.funnyguilds.event.guild.GuildExtendValidityEvent
import org.bukkit.event.Event
import pl.funnyskaddon.skript.effect.GuildValueEffect
import pl.funnyskaddon.skript.getGuild
import pl.funnyskaddon.skript.getValue
import pl.funnyskaddon.skript.registration.Registration.registerEffect
import java.time.Duration

@Name("Add To Expiration Time")
@Description("Adds a given amount of time to the guild's expiration date")
@Since("2.0.0")
@Examples(
    "add 1 day to \"FajnaNazwa\" guild expiration time",
)
class GuildAddToExpirationTimeEffect : GuildValueEffect<Timespan>(true) {

    companion object {
        init {
            registerEffect<GuildAddToExpirationTimeEffect>(
                "add %timespan% to %guild%['s] (expiration|validity) [(date|time)]",
                "add %timespan% to %string%['s] [guild] (expiration|validity) [(date|time)]",
                "add %timespan% to %offlineplayer%['s] [guild] (expiration|validity) [(date|time)]",
                "add %timespan% to %location%['s] [guild] (expiration|validity) [(date|time)]",
                "add %timespan% to %block%['s] [guild] (expiration|validity) [(date|time)]",
                "add %timespan% to %object%['s] [guild] (expiration|validity) [(date|time)]"
            )
        }
    }

    override fun execute(event: Event) {
        event.getGuild(guildExpression).peek { guild ->
            event.getValue(valueExpression)
                .map { it.getAs(TimePeriod.MILLISECOND) }
                .map(Duration::ofMillis)
                .filter { value ->
                    SimpleEventHandler.handle(
                        GuildExtendValidityEvent(
                            FunnyEvent.EventCause.CONSOLE,
                            null,
                            guild,
                            value
                        )
                    )
                }
                .peek { value -> guild.validity += value }
        }
    }

}
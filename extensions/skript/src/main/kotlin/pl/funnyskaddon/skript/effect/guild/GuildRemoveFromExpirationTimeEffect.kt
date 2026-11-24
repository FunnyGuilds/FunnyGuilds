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

@Name("Remove Expiration Time")
@Description("Removes a given amount of time from the guild's expiration date")
@Since("2.0.0")
@Examples(
    "remove 1 day from \"FajnaNazwa\" guild expiration time",
)
class GuildRemoveFromExpirationTimeEffect : GuildValueEffect<Timespan>(true) {

    companion object {
        init {
            registerEffect<GuildRemoveFromExpirationTimeEffect>(
                "remove %timespan% from %guild%['s] (expiration|validity) [time]",
                "remove %timespan% from %string%['s] [guild] (expiration|validity) [time]",
                "remove %timespan% from %offlineplayer%['s] [guild] (expiration|validity) [time]",
                "remove %timespan% from %location%['s] [guild] (expiration|validity) [time]",
                "remove %timespan% from %block%['s] [guild] (expiration|validity) [time]",
                "remove %timespan% from %object%['s] [guild] (expiration|validity) [time]"
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
                            -value
                        )
                    )
                }
                .peek { value -> guild.validity = guild.validity - value }
        }
    }

}

private operator fun Duration.unaryMinus(): Duration? {
    return Duration.ofMillis(-this.toMillis())
}

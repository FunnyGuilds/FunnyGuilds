package pl.funnyskaddon.skript.effect.guild

import pl.funnyskaddon.extension.peek

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.util.Date
import net.dzikoysk.funnyguilds.event.FunnyEvent
import net.dzikoysk.funnyguilds.event.SimpleEventHandler
import net.dzikoysk.funnyguilds.event.guild.GuildExtendValidityEvent
import org.bukkit.event.Event
import pl.funnyskaddon.skript.effect.GuildValueEffect
import pl.funnyskaddon.skript.getGuild
import pl.funnyskaddon.skript.getValue
import pl.funnyskaddon.skript.registration.Registration.registerEffect
import java.time.Duration
import java.time.Duration.ofMillis
import java.time.Instant


@Name("Set Expiration Time")
@Description("Sets the guild's expiration date")
@Since("2.0.0")
@Examples(
    "set {_tomorrow} to 1 day later",
    "set expiration date of \"FajnaNazwa\" guild to {_tomorrow}",
)
class GuildSetExpirationDateEffect : GuildValueEffect<Date>(false) {

    companion object {
        init {
            registerEffect<GuildSetExpirationDateEffect>(
                "set (expiration|validity) [(date|time)] of %guild% to %date%",
                "set (expiration|validity) [(date|time)] of %string%['s] [guild] to %date%",
                "set (expiration|validity) [(date|time)] of %offlineplayer%['s] [guild] to %date%",
                "set (expiration|validity) [(date|time)] of %location%['s] [guild] to %date%",
                "set (expiration|validity) [(date|time)] of %block%['s] [guild] to %date%",
                "set (expiration|validity) [(date|time)] of %object%['s] [guild] to %date%"
            )
        }
    }

    override fun execute(event: Event) {
        event.getGuild(guildExpression).peek { guild ->
            event.getValue(valueExpression)
                .map(Date::getTime)
                .map(Instant::ofEpochMilli)
                .map { newValidity -> newValidity - guild.validity }
                .filter { difference ->
                    return@filter SimpleEventHandler.handle(
                        GuildExtendValidityEvent(
                            FunnyEvent.EventCause.CONSOLE,
                            null,
                            guild,
                            difference
                        )
                    )
                }
                .peek { difference ->
                    guild.validity = guild.validity + difference
                }
        }
    }
}

private operator fun Instant.minus(newValidity: Instant): Duration {
    return ofMillis(this.toEpochMilli() - newValidity.toEpochMilli())
}

private operator fun Duration.minus(validity: Instant?): Duration? {
    return ofMillis(this.toMillis() - validity!!.toEpochMilli())
}
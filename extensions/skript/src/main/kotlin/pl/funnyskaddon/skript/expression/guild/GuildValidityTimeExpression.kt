package pl.funnyskaddon.skript.expression.guild

import pl.funnyskaddon.skript.registration.Registration.guildSources
import pl.funnyskaddon.skript.registration.Registration.PROPERTY
import pl.funnyskaddon.skript.registration.Registration.registerExpression
import pl.funnyskaddon.extension.orNull

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.util.Timespan
import org.bukkit.event.Event
import pl.funnyskaddon.skript.expression.GuildExpression
import pl.funnyskaddon.skript.getGuild
import java.time.Duration
import java.time.Instant
import java.util.*

@Name("Guild Validity Time")
@Since("2.0.0")
@Description("Returns the remaining validity time of a guild")
@Examples(
    "send \"%\"AC4U\" guild validity time%\"",
    "set {_time} to \"AC4U\" guild validity time"
)
class GuildValidityTimeExpression : GuildExpression<Timespan>(Timespan::class.java) {

    companion object {
        init {
            registerExpression<GuildValidityTimeExpression, Timespan>(
                PROPERTY,
                *guildSources("['s] validity time")
            )
        }
    }

    override fun get(event: Event): Array<Timespan>? {
        return event.getGuild(guildExpression)
            .map { guild -> Duration.between(Instant.now(), guild.validity) }
            .map { duration -> Timespan(duration.toMillis()) }
            .map { value -> arrayOf(value) }
            .orNull()
    }


}
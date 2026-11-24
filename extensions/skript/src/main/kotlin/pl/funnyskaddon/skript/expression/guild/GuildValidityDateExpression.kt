package pl.funnyskaddon.skript.expression.guild

import pl.funnyskaddon.skript.registration.Registration.guildSources
import pl.funnyskaddon.skript.registration.Registration.PROPERTY
import pl.funnyskaddon.skript.registration.Registration.registerExpression
import pl.funnyskaddon.extension.orNull

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.util.Date
import net.dzikoysk.funnyguilds.guild.Guild
import org.bukkit.event.Event
import pl.funnyskaddon.skript.expression.GuildExpression
import pl.funnyskaddon.skript.getGuild
import java.time.Instant

@Name("Guild Validity Date")
@Since("2.0.0")
@Description("Returns the date until which a guild is valid")
@Examples(
    "send \"%\"AC4U\" guild validity date%\"",
    "set {_time} to \"AC4U\" guild validity date"
)
class GuildValidityDateExpression : GuildExpression<Date>(Date::class.java) {

    companion object {
        init {
            registerExpression<GuildValidityDateExpression, Date>(
                PROPERTY,
                *guildSources("['s] validity date")
            )
        }
    }

    override fun get(event: Event): Array<Date>? {
        return event.getGuild(guildExpression)
            .map(Guild::getValidity)
            .map(Instant::toEpochMilli)
            .map { validity -> Date(validity) }
            .map { value -> arrayOf(value) }
            .orNull()
    }


}
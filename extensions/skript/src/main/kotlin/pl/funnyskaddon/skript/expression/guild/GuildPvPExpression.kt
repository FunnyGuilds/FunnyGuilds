package pl.funnyskaddon.skript.expression.guild

import pl.funnyskaddon.skript.registration.Registration.guildSources
import pl.funnyskaddon.skript.registration.Registration.PROPERTY
import pl.funnyskaddon.skript.registration.Registration.registerExpression
import pl.funnyskaddon.extension.orNull

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.guild.Guild
import org.bukkit.event.Event
import pl.funnyskaddon.skript.expression.GuildExpression
import pl.funnyskaddon.skript.getGuild

@Name("Guild PvP")
@Since("2.0.0")
@Description("Returns whether PvP in a guild is enabled or disabled")
@Examples(
    "send \"%\"AC4U\" guild pvp%\"",
    "set {_pvpStatus} to \"AC4U\" guild pvp"
)
class GuildPvPExpression : GuildExpression<Boolean>(Boolean::class.javaObjectType) {

    companion object {
        init {
            registerExpression<GuildPvPExpression, Boolean>(
                PROPERTY,
                *guildSources("['s] pvp [[is] enabled]")
            )
        }
    }

    override fun get(event: Event): Array<Boolean>? {
        return event.getGuild(guildExpression)
            .map(Guild::hasPvPEnabled)
            .map { value -> arrayOf(value) }
            .orNull()
    }


}
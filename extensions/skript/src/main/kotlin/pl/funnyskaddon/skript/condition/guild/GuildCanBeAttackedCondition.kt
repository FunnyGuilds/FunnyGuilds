package pl.funnyskaddon.skript.condition.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.guild.Guild
import org.bukkit.event.Event
import pl.funnyskaddon.skript.condition.GuildCondition
import pl.funnyskaddon.skript.registration.Registration.registerCondition
import pl.funnyskaddon.skript.getGuild
@Name("Guild Can Be Attacked")
@Since("2.0.0")
@Description("Checks if the guild can be attacked")
@Examples(
    "if guild \"AC4U\" can be attacked:",
    "if guild \"AC4U\" can't be attacked:"
)
class GuildCanBeAttackedCondition : GuildCondition(7) {

    companion object {
        init {
            registerCondition<GuildCanBeAttackedCondition>(
                "%guild% can be attacked",
                "%string% [guild] can be attacked",
                "%offlineplayer%['s] guild can be attacked",
                "guild at %location% can be attacked",
                "guild at %block% can be attacked",
                "%object%['s] guild can be attacked",
                "%guild% (cannot|can't) be attacked",
                "%string% [guild] (cannot|can't) be attacked",
                "%offlineplayer%['s] guild (cannot|can't) be attacked",
                "guild at %location% (cannot|can't) be attacked",
                "guild at %block% (cannot|can't) be attacked",
                "%object%['s] guild (cannot|can't) be attacked",
            )
        }
    }

    override fun check(event: Event): Boolean {
        return event.getGuild(guildExpression)
            .map(Guild::canBeAttacked)
            .orElse(false)
            .xor(isNegated)
    }

}
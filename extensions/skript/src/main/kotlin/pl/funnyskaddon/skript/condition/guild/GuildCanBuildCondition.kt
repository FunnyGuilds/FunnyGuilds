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
@Name("Guild Can Build")
@Since("2.0.0")
@Description("Checks if the guild can build")
@Examples(
    "if guild \"AC4U\" can build:",
    "if guild \"AC4U\" can't be build:"
)
class GuildCanBuildCondition : GuildCondition(7) {

    companion object {
        init {
            registerCondition<GuildCanBuildCondition>(
                "%guild% can build",
                "%string% [guild] can build",
                "%offlineplayer%['s] guild can build",
                "guild at %location% can build",
                "guild at %block% can build",
                "%object%['s] guild can build",
                "%guild% (can't|cannot|can not) build",
                "%string% [guild] (can't|cannot|can not) build",
                "%offlineplayer%['s] guild (can't|cannot|can not) build",
                "guild at %location% (can't|cannot|can not) build",
                "guild at %block% (can't|cannot|can not) build",
                "%object%['s] guild (can't|cannot|can not) build",
            )
        }
    }

    override fun check(event: Event): Boolean {
        return event.getGuild(guildExpression)
            .map(Guild::canBuild)
            .orElse(false)
            .xor(isNegated)
    }

}
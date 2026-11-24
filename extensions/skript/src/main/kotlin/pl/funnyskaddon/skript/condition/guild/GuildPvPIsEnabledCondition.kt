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
@Name("Guild PvP Is Enabled")
@Since("2.0.0")
@Description("Checks if the guild has PvP enabled")
@Examples(
    "if guild \"AC4U\" pvp is enabled:",
    "if guild \"AC4U\" pvp is disabled:"
)
class GuildPvPIsEnabledCondition : GuildCondition(7) {

    companion object {
        init {
            registerCondition<GuildPvPIsEnabledCondition>(
                "%guild%['s] pvp is (enabled|on)",
                "%string% [guild]['s] pvp is (enabled|on)",
                "%offlineplayer%['s] guild['s] pvp is (enabled|on)",
                "guild at %location% pvp is (enabled|on)",
                "guild at %block% pvp is (enabled|on)",
                "%object%['s] guild['s] pvp is (enabled|on)",
                "%guild%['s] pvp is (disabled|off)",
                "%string% [guild]['s] pvp is (disabled|off)",
                "%offlineplayer%['s] guild['s] pvp is (disabled|off)",
                "guild at %location% pvp is (disabled|off)",
                "guild at %block% pvp is (disabled|off)",
                "%object%['s] guild['s] pvp is (disabled|off)",
            )
        }
    }

    override fun check(event: Event): Boolean {
        return event.getGuild(guildExpression)
            .map(Guild::hasPvPEnabled)
            .orElse(false)
            .xor(isNegated)
    }

}
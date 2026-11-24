package pl.funnyskaddon.skript.condition.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import org.bukkit.Location
import org.bukkit.event.Event
import pl.funnyskaddon.extension.isLocationInGuildRegion
import pl.funnyskaddon.skript.condition.GuildValueCondition
import pl.funnyskaddon.skript.registration.Registration.registerCondition
import pl.funnyskaddon.skript.getGuild
import pl.funnyskaddon.skript.getValue
@Name("Is Location In Guild Region")
@Since("2.0.0")
@Description("Checks if the location is in the guild's region")
@Examples(
    "if location (location at (100, 100, 100) in world \"world\") is in guild \"AC4U\" region:",
    "if location (location at (100, 100, 100) in world \"world\") is not in guild \"AC4U\" region:"
)
class GuildIsLocationInRegion : GuildValueCondition<Location>(true, 7) {

    companion object {
        init {
            registerCondition<GuildIsLocationInRegion>(
                "[location] %location% is in %guild%['s] region",
                "[location] %location% is in %string% [guild]['s] region",
                "[location] %location% is in %offlineplayer%['s] guild['s] region",
                "[location] %location% is in guild at %location% region",
                "[location] %location% is in guild at %block% region",
                "[location] %location% is in %object%['s] guild['s] region",
                "[location] %location% is(n't| not) in %guild%['s] region",
                "[location] %location% is(n't| not) in %string% [guild]['s] region",
                "[location] %location% is(n't| not) in %offlineplayer%['s] guild['s] region",
                "[location] %location% is(n't| not) in guild at %location% region",
                "[location] %location% is(n't| not) in guild at %block% region",
                "[location] %location% is(n't| not) in %object%['s] guild['s] region",
            )
        }
    }

    override fun check(event: Event): Boolean {
        return event.getGuild(guildExpression)
            .map { guild -> guild.isLocationInGuildRegion(event.getValue(valueExpression)) }
            .orElse(false)
            .xor(isNegated)
    }

}
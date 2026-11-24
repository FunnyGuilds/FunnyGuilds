package pl.funnyskaddon.skript.condition.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import org.bukkit.event.Event
import pl.funnyskaddon.skript.condition.GuildCondition
import pl.funnyskaddon.skript.registration.Registration.registerCondition
import pl.funnyskaddon.skript.getGuild
@Name("Guild Does Exist")
@Since("2.0.0")
@Description("Checks if the guild exists")
@Examples(
    "if guild \"AC4U\" exists:",
    "if guild \"AC4U\" doesn't exists:"
)
class GuildDoesExistCondition : GuildCondition(7) {

    companion object {
        init {
            registerCondition<GuildDoesExistCondition>(
                "%guild% [does] exist[s]",
                "%string% [guild] [does] exist[s]",
                "guild at %location% [does] exist[s]",
                "guild at %block% [does] exist[s]",
                "%offlineplayer%['s] guild [does] exist[s]",
                "%object%['s] guild [does] exist[s]",
                "%guild% does(n't|not) exist[s]",
                "%string% [guild] does(n't|not) exist[s]",
                "guild at %location% does(n't|not) exist[s]",
                "guild at %block% does(n't|not) exist[s]",
                "%offlineplayer%['s] guild does(n't|not)exist[s]",
                "%object%['s] guild does(n't|not) exist[s]",
            )
        }
    }

    override fun check(event: Event): Boolean {
        return event.getGuild(guildExpression)
            .isPresent
            .xor(isNegated)
    }

}
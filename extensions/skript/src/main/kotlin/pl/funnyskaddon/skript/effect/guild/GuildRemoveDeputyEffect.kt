package pl.funnyskaddon.skript.effect.guild

import pl.funnyskaddon.extension.peek

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import org.bukkit.OfflinePlayer
import org.bukkit.event.Event
import pl.funnyskaddon.skript.effect.GuildValueEffect
import pl.funnyskaddon.skript.getGuild
import pl.funnyskaddon.skript.getUser
import pl.funnyskaddon.skript.registration.Registration.registerEffect

@Name("Remove Guild Deputy")
@Description("Removes a deputy from the guild")
@Since("2.0.0")
@Examples(
    "remove player from \"AC4U\" guild deputies",
)
class GuildRemoveDeputyEffect : GuildValueEffect<OfflinePlayer>(true) {

    companion object {
        init {
            registerEffect<GuildRemoveDeputyEffect>(
                "remove %offlineplayer% from %guild%['s] deputies",
                "remove %offlineplayer% from %string%['s] [guild] deputies",
                "remove %offlineplayer% from %offlineplayer%['s] [guild] deputies",
                "remove %offlineplayer% from %location%['s] [guild] deputies",
                "remove %offlineplayer% from %block%['s] [guild] deputies",
                "remove %offlineplayer% from %object%['s] [guild] deputies"
            )
        }
    }

    override fun execute(event: Event) {
        event.getGuild(guildExpression)
            .peek { guild ->
                event.getUser(valueExpression)
                    .peek userPeek@{ user ->
                        guild.removeDeputy(user)
                    }
            }
    }

}
package pl.funnyskaddon.skript.effect.guild

import pl.funnyskaddon.extension.peek

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.event.FunnyEvent
import net.dzikoysk.funnyguilds.event.SimpleEventHandler
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberDeputyEvent
import org.bukkit.OfflinePlayer
import org.bukkit.event.Event
import pl.funnyskaddon.skript.effect.GuildValueEffect
import pl.funnyskaddon.skript.getGuild
import pl.funnyskaddon.skript.getUser
import pl.funnyskaddon.skript.registration.Registration.registerEffect


@Name("Add Guild Deputy")
@Description("Adds a deputy to the guild")
@Since("2.0.0")
@Examples(
    "add player to \"AC4U\" guild deputies",
)
class GuildAddDeputyEffect : GuildValueEffect<OfflinePlayer>(true) {

    companion object {
        init {
            registerEffect<GuildAddDeputyEffect>(
                "add %offlineplayer% to %guild%['s] deputies",
                "add %offlineplayer% to %string%['s] [guild] deputies",
                "add %offlineplayer% to %offlineplayer%['s] guild deputies",
                "add %offlineplayer% to %location%['s] guild deputies",
                "add %offlineplayer% to %block%['s] guild deputies",
                "add %offlineplayer% to %object%['s] guild deputies"
            )
        }
    }

    override fun execute(event: Event) {
        event.getGuild(guildExpression)
            .peek { guild ->
                event.getUser(valueExpression)
                    .peek userPeek@{ user ->
                        if (!SimpleEventHandler.handle(
                                GuildMemberDeputyEvent(
                                    FunnyEvent.EventCause.CONSOLE,
                                    null,
                                    guild,
                                    user
                                )
                            )
                        ) {
                            return@userPeek
                        }

                        guild.addDeputy(user)
                    }
            }
    }

}
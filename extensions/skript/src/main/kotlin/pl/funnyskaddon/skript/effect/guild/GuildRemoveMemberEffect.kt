package pl.funnyskaddon.skript.effect.guild

import pl.funnyskaddon.extension.peek

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.event.FunnyEvent
import net.dzikoysk.funnyguilds.event.SimpleEventHandler
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberKickEvent
import org.bukkit.OfflinePlayer
import org.bukkit.event.Event
import pl.funnyskaddon.skript.effect.GuildValueEffect
import pl.funnyskaddon.skript.getGuild
import pl.funnyskaddon.skript.getUser
import pl.funnyskaddon.skript.registration.Registration.registerEffect


@Name("Remove Guild Member")
@Description("Removes a member from the guild")
@Since("2.0.0")
@Examples(
    "remove player from \"AC4U\" guild members",
)
class GuildRemoveMemberEffect : GuildValueEffect<OfflinePlayer>(true) {

    companion object {
        init {
            registerEffect<GuildRemoveMemberEffect>(
                "remove %offlineplayer% from %guild%['s] members",
                "remove %offlineplayer% from %string%['s] [guild] members",
                "remove %offlineplayer% from %offlineplayer%['s] [guild] members",
                "remove %offlineplayer% from %location%['s] [guild] members",
                "remove %offlineplayer% from %block%['s] [guild] members",
                "remove %offlineplayer% from %object%['s] [guild] members"
            )
        }
    }

    override fun execute(event: Event) {
        event.getGuild(guildExpression)
            .peek { guild ->
                event.getUser(valueExpression)
                    .peek userPeek@{ user ->
                        if (!SimpleEventHandler.handle(
                                GuildMemberKickEvent(
                                    FunnyEvent.EventCause.CONSOLE,
                                    null,
                                    guild,
                                    user
                                )
                            )
                        ) {
                            return@userPeek
                        }

                        guild.removeMember(user)
                        user.removeGuild()
                    }
            }
    }

}
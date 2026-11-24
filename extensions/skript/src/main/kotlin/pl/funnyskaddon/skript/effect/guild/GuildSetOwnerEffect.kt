package pl.funnyskaddon.skript.effect.guild

import pl.funnyskaddon.extension.peek

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.event.FunnyEvent
import net.dzikoysk.funnyguilds.event.SimpleEventHandler
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberLeaderEvent
import org.bukkit.OfflinePlayer
import org.bukkit.event.Event
import pl.funnyskaddon.skript.effect.GuildValueEffect
import pl.funnyskaddon.skript.getGuild
import pl.funnyskaddon.skript.getUser
import pl.funnyskaddon.skript.registration.Registration.registerEffect

@Name("Set Guild Owner")
@Description("Sets the guild's owner")
@Since("2.0.0")
@Examples(
    "set \"AC4U\" guild owner to player",
)
class GuildSetOwnerEffect : GuildValueEffect<OfflinePlayer>(false) {

    companion object {
        init {
            registerEffect<GuildSetOwnerEffect>(
                "set %guild%['s] owner to %offlineplayer%",
                "set %string%['s] [guild] owner to %offlineplayer%",
                "set %offlineplayer%['s] [guild] owner to %offlineplayer%",
                "set %location%['s] [guild] owner to %offlineplayer%",
                "set %block%['s] [guild] owner to %offlineplayer%",
                "set %object%['s] [guild] owner to %offlineplayer%",
            )
        }
    }

    override fun execute(event: Event) {
        event.getGuild(guildExpression)
            .peek { guild ->
                event.getUser(valueExpression)
                    .peek userPeek@{ user ->
                        if (!SimpleEventHandler.handle(
                                GuildMemberLeaderEvent(
                                    FunnyEvent.EventCause.CONSOLE,
                                    null,
                                    guild,
                                    user
                                )
                            )
                        ) {
                            return@userPeek
                        }

                        guild.owner = user
                    }
            }
    }

}
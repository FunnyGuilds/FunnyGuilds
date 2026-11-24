package pl.funnyskaddon.skript.effect.guild

import pl.funnyskaddon.extension.peek
import pl.funnyskaddon.extension.orNull

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.Expression
import net.dzikoysk.funnyguilds.event.FunnyEvent
import net.dzikoysk.funnyguilds.event.SimpleEventHandler
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberJoinEvent
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberLeaveEvent
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.user.User
import org.bukkit.OfflinePlayer
import org.bukkit.event.Event
import pl.funnyskaddon.extension.getGuild
import pl.funnyskaddon.skript.effect.GuildValueEffect
import pl.funnyskaddon.skript.getUser
import pl.funnyskaddon.skript.getValue
import pl.funnyskaddon.skript.registration.Registration.registerEffect


@Name("Add Guild Member")
@Description(
    "Adds a player to the guild<br>",
    "Alternative: <b>set %offlineplayer%['s] guild to %object%</b>",
)
@Since("2.0.0")
@Examples(
    "add player to \"AC4U\" guild members",
)
class GuildAddMemberEffect : GuildValueEffect<OfflinePlayer>(true) {

    companion object {
        init {
            registerEffect<GuildAddMemberEffect>(
                "add %offlineplayer% to %guild%['s] members",
                "add %offlineplayer% to %string%['s] [guild]['s] members",
                "add %offlineplayer% to %offlineplayer%['s] [guild] members",
                "add %offlineplayer% to %location%['s] [guild] members",
                "add %offlineplayer% to %block%['s] [guild] members",
                "add %offlineplayer% to %object%['s] [guild] members"
            )
        }

        fun execute(event: Event, guildExpression: Expression<Any>, playerExpression: Expression<OfflinePlayer>) {
            event.getUser(playerExpression)
                .peek { user ->
                    val guild = event.getValue(guildExpression)
                        .flatMap(Any::getGuild)
                        .orNull()

                    if (user.hasGuild()) {
                        if (user.isOwner) {
                            return@peek
                        }

                        if (guild == null) {
                            removerFromGuild(user)
                        } else {
                            if (user.guild == guild) {
                                return@peek
                            }

                            if (removerFromGuild(user)) {
                                addMemberToGuild(user, guild)
                            }
                        }
                    } else {
                        if (guild == null) {
                            return@peek
                        }

                        addMemberToGuild(user, guild)
                    }
                }
        }

        private fun addMemberToGuild(member: User, guild: Guild): Boolean {
            if (!SimpleEventHandler.handle(
                    GuildMemberJoinEvent(FunnyEvent.EventCause.CONSOLE, null, guild, member)
                )
            ) {
                return false
            }

            guild.addMember(member)
            member.setGuild(guild)

            return true
        }

        private fun removerFromGuild(member: User): Boolean {
            if (!SimpleEventHandler.handle(
                    GuildMemberLeaveEvent(FunnyEvent.EventCause.CONSOLE, null, member.guild.get(), member)
                )
            ) {
                return false
            }
            member.guild.peek { guild ->
                member.removeGuild()
                guild.removeMember(member)
            }

            return true
        }
    }

    public override fun execute(event: Event) {
        execute(event, guildExpression, valueExpression)
    }

}
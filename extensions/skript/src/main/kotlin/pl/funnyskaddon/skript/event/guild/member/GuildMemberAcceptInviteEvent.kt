package pl.funnyskaddon.skript.event.guild.member

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Member Accept Invitation")
@Description("Called when a player accepts a guild invitation")
@Since("2.0.0")
@Examples(
    "on guild member accept invitation:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%member%\"",
    "    send \"%guild%\""
)
class GuildMemberAcceptInviteEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildMemberAcceptInviteEvent::class.java, net.dzikoysk.funnyguilds.event.guild.member.GuildMemberAcceptInviteEvent::class.java, "Guild Member Accept Invitation", "guild member accept (invite|invitation)")
        }
    }

}

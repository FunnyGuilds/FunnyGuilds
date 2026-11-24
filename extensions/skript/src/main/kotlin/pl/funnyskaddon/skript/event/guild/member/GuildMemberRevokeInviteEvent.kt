package pl.funnyskaddon.skript.event.guild.member

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Member Revoke Invitation")
@Description("Called when a player rejects a guild invitation")
@Since("2.0.0")
@Examples(
    "on guild member revoke invitation:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%member%\"",
    "    send \"%guild%\""
)
class GuildMemberRevokeInviteEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildMemberRevokeInviteEvent::class.java, net.dzikoysk.funnyguilds.event.guild.member.GuildMemberAcceptInviteEvent::class.java, "Guild Member Revoke Invitation", "guild member revoke (invite|invitation)")
        }
    }

}

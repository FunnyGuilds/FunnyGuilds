package pl.funnyskaddon.skript.event.guild.member

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Member Invite")
@Description("Called when a player is invited to a guild")
@Since("2.0.0")
@Examples(
    "on guild member invite:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%member%\"",
    "    send \"%guild%\""
)
class GuildMemberInviteEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildMemberInviteEvent::class.java, net.dzikoysk.funnyguilds.event.guild.member.GuildMemberInviteEvent::class.java, "Guild Member Invite", "guild member (invite|invitation)")
        }
    }

}

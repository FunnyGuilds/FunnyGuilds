package pl.funnyskaddon.skript.event.guild.ally

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Revoke Ally Invitation")
@Description("Called when a guild rejects an ally invitation")
@Since("2.0.0")
@Examples(
    "on guild revoke ally invitation:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%guild%\"",
    "    send \"%ally%\""
)
class GuildRevokeAllyInvitationEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildRevokeAllyInvitationEvent::class.java, net.dzikoysk.funnyguilds.event.guild.ally.GuildRevokeAllyInvitationEvent::class.java, "Guild Revoke Ally Invitation", "guild revoke ally (invite|invitation)")
        }
    }

}

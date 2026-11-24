package pl.funnyskaddon.skript.event.guild.ally

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Send Ally Invitation")
@Description("Called when an ally invitation is sent")
@Since("2.0.0")
@Examples(
    "on guild send ally invitation:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%guild%\"",
    "    send \"%ally%\""
)
class GuildSendAllyInvitationEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildSendAllyInvitationEvent::class.java, net.dzikoysk.funnyguilds.event.guild.ally.GuildSendAllyInvitationEvent::class.java, "Guild Send Ally Invitation", "guild send ally (invite|invitation)")
        }
    }

}

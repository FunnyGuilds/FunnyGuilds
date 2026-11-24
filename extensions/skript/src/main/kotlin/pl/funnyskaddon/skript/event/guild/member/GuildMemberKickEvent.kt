package pl.funnyskaddon.skript.event.guild.member

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Member Kick")
@Description("Called when a player is kicked from a guild")
@Since("2.0.0")
@Examples(
    "on guild kick:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%member%\"",
    "    send \"%guild%\""
)
class GuildMemberKickEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildMemberKickEvent::class.java, net.dzikoysk.funnyguilds.event.guild.member.GuildMemberKickEvent::class.java, "Guild Member Kick", "guild member kick")
        }
    }

}

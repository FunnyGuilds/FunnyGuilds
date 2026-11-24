package pl.funnyskaddon.skript.event.guild.member

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Member Leave")
@Description("Called when a player leaves a guild")
@Since("2.0.0")
@Examples(
    "on guild member leave",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%member%\"",
    "    send \"%guild%\""
)
class GuildMemberLeaveEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildMemberLeaveEvent::class.java, net.dzikoysk.funnyguilds.event.guild.member.GuildMemberLeaveEvent::class.java, "Guild Member Leave", "guild member leave")
        }
    }

}

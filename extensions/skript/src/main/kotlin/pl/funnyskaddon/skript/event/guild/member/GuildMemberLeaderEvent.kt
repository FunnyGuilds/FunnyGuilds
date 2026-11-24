package pl.funnyskaddon.skript.event.guild.member

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Member Leader")
@Description("Called when a new guild owner is chosen")
@Since("2.0.0")
@Examples(
    "on guild member owner:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%member%\"",
    "    send \"%guild%\""
)
class GuildMemberLeaderEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildMemberLeaderEvent::class.java, net.dzikoysk.funnyguilds.event.guild.member.GuildMemberLeaderEvent::class.java, "Guild Member Leader", "guild member (leader|owner)", "guild change (leader|owner)")
        }
    }

}

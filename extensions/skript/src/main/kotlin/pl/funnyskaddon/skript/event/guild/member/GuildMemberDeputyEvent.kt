package pl.funnyskaddon.skript.event.guild.member

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Member Deputy")
@Description("Called when a new guild deputy is chosen")
@Since("2.0.0")
@Examples(
    "on guild member deputy:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%member%\"",
    "    send \"%guild%\""
)
class GuildMemberDeputyEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildMemberDeputyEvent::class.java, net.dzikoysk.funnyguilds.event.guild.member.GuildMemberDeputyEvent::class.java, "Guild Member Deputy", "guild member deputy")
        }
    }

}

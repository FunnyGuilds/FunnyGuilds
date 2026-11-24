package pl.funnyskaddon.skript.event.guild.member

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Member Join")
@Description("Called when a player joins a guild")
@Since("2.0.0")
@Examples(
    "on guild member join:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%member%\"",
    "    send \"%guild%\""
)
class GuildMemberJoinEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildMemberJoinEvent::class.java, net.dzikoysk.funnyguilds.event.guild.member.GuildMemberJoinEvent::class.java, "Guild Member Join", "guild member join")
        }
    }

}

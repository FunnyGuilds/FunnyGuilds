package pl.funnyskaddon.skript.event.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Unban")
@Description("Called when a guild gets unbanned")
@Since("2.0.0")
@Examples(
    "on guild unban:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%guild%\""
)
class GuildUnbanEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildUnbanEvent::class.java, net.dzikoysk.funnyguilds.event.guild.GuildUnbanEvent::class.java, "Guild Unban", "guild unban")
        }
    }

}

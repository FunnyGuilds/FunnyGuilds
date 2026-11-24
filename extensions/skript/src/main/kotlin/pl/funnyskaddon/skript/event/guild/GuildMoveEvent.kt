package pl.funnyskaddon.skript.event.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Move")
@Description("Called when the guild heart location changes")
@Since("2.0.0")
@Examples(
    "on guild move:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%guild%\"",
    "    send \"%new location%\""
)
class GuildMoveEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildMoveEvent::class.java, net.dzikoysk.funnyguilds.event.guild.GuildMoveEvent::class.java, "Guild Move", "guild move")
        }
    }

}

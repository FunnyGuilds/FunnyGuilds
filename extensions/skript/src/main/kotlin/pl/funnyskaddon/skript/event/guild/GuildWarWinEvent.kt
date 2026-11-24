package pl.funnyskaddon.skript.event.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild War Win")
@Description("Called when a guild wins a war against another guild")
@Since("3.0.0")
@Examples(
    "on guild war win:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%guild%\""
)
class GuildWarWinEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildWarWinEvent::class.java, net.dzikoysk.funnyguilds.event.guild.GuildWarWinEvent::class.java, "Guild War Win", "guild war win")
        }
    }

}

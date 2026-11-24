package pl.funnyskaddon.skript.event.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Extend Validity")
@Description("Called when the guild validity is extended")
@Since("2.0.0")
@Examples(
    "on guild extend validity:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%guild%\"",
    "    send \"%extend time%\""
)
class GuildExtendValidityEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildExtendValidityEvent::class.java, net.dzikoysk.funnyguilds.event.guild.GuildExtendValidityEvent::class.java, "Guild Extend Validity", "guild extend validity")
        }
    }

}

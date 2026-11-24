package pl.funnyskaddon.skript.event.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Region Enter")
@Description("Called when a player enters the guild region")
@Since("2.0.0")
@Examples(
    "on gild region enter:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%guild%\""
)
class GuildRegionEnterEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildRegionEnterEvent::class.java, net.dzikoysk.funnyguilds.event.guild.GuildRegionEnterEvent::class.java, "Guild Region Enter", "guild region enter")
        }
    }

}

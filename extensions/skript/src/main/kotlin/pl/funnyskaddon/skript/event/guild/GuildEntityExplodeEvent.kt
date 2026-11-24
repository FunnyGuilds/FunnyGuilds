package pl.funnyskaddon.skript.event.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Entity Explode")
@Description("Called when something explodes on the guild territory")
@Since("2.0.0")
@Examples(
    "on guild entity explode:",
    "    send \"%cause%\"",
    "    send \"%affected blocks%\""
)
class GuildEntityExplodeEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildEntityExplodeEvent::class.java, net.dzikoysk.funnyguilds.event.guild.GuildEntityExplodeEvent::class.java, "Guild Entity Explode", "guild entity explode")
        }
    }

}

package pl.funnyskaddon.skript.event.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Rename")
@Description("Called when the guild name changes")
@Since("2.0.0")
@Examples(
    "on guild rename:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%guild%\"",
    "    send \"%old name%\"",
    "    send \"%new name%\""
)
class GuildRenameEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildRenameEvent::class.java, net.dzikoysk.funnyguilds.event.guild.GuildPreRenameEvent::class.java, "Guild Rename", "guild rename")
        }
    }

}

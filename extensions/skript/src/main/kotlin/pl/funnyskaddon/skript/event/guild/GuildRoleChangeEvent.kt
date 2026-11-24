package pl.funnyskaddon.skript.event.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Role Change")
@Description("Called when a guild's role policy changes")
@Since("3.0.0")
@Examples(
    "on guild role change:",
    "    send \"%player%\"",
    "    send \"%guild%\""
)
class GuildRoleChangeEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildRoleChangeEvent::class.java, net.dzikoysk.funnyguilds.event.guild.GuildRoleChangeEvent::class.java, "Guild Role Change", "guild (role|rank) change")
        }
    }

}

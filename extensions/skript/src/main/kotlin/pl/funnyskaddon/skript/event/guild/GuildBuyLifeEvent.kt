package pl.funnyskaddon.skript.event.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.util.SimpleEvent
import pl.funnyskaddon.skript.registration.Registration.registerEvent

@Name("Guild Buy Life")
@Description("Called when a player buys an extra life for their guild")
@Since("3.0.0")
@Examples(
    "on guild buy life:",
    "    send \"%cause%\"",
    "    send \"%player%\"",
    "    send \"%guild%\""
)
class GuildBuyLifeEvent : SimpleEvent() {

    companion object {
        init {
            registerEvent(GuildBuyLifeEvent::class.java, net.dzikoysk.funnyguilds.event.guild.GuildBuyLifeEvent::class.java, "Guild Buy Life", "guild (buy|purchase) life")
        }
    }

}

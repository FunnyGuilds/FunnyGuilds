package pl.funnyskaddon.skript.effect.player

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import org.bukkit.event.Event
import pl.funnyskaddon.skript.effect.PlayerEffect
import pl.funnyskaddon.skript.effect.guild.GuildAddMemberEffect
import pl.funnyskaddon.skript.registration.Registration.registerEffect

@Name("Set Guild")
@Description(
    "Sets the player's guild (joins them to it)",
    "Alternative: add %offlineplayer% to %object%(|'s) guild members",
)
@Since("2.0.0")
@Examples(
    "set player's guild to \"AC4U\"",
)
class PlayerSetGuildEffect : PlayerEffect<Any>(false) {

    companion object {

        init {
            registerEffect<PlayerSetGuildEffect>("set %offlineplayer%['s] guild to %object%")
        }
    }

    override fun execute(event: Event) {
        GuildAddMemberEffect.execute(event, valueExpression, playerExpression)
    }

}
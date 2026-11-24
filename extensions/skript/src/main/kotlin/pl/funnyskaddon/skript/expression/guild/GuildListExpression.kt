package pl.funnyskaddon.skript.expression.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.guild.Guild
import org.bukkit.event.Event
import pl.funnyskaddon.skript.registration.Registration.PROPERTY
import pl.funnyskaddon.skript.registration.Registration.registerExpression
import pl.funnyskaddon.skript.expression.FunnyExpression

@Name("All Guilds")
@Since("2.0.0")
@Description("Returns all guilds")
@Examples(
    "loop all guilds:",
    "    send \"%loop-value%\""
)
class GuildListExpression : FunnyExpression<Guild>(Guild::class.java) {

    companion object {
        init {
            registerExpression<GuildListExpression, Guild>(
                PROPERTY,
                "[all] guilds [list]"
            )
        }
    }

    override fun get(event: Event): Array<Guild?> {
        return FunnyGuilds.getInstance().guildManager.guilds.toTypedArray()
    }

    override fun isSingle(): Boolean {
        return false
    }



}
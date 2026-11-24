package pl.funnyskaddon.skript.expression.guild

import pl.funnyskaddon.skript.registration.Registration.PROPERTY
import pl.funnyskaddon.skript.registration.Registration.registerExpression
import pl.funnyskaddon.extension.toStream

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.guild.Guild
import org.bukkit.entity.Player
import org.bukkit.event.Event
import pl.funnyskaddon.extension.getPlayersInGuildRegion
import pl.funnyskaddon.skript.expression.GuildExpression
import pl.funnyskaddon.skript.getGuild
import java.util.stream.Collectors

@Name("Players In Guild Region")
@Since("2.0.0")
@Description("Returns the list of players inside a guild's region")
@Examples(
    "loop players in (location at (100, 100, 100) in world \"world\") guild region:",
    "    send \"%loop-value%\""
)
class GuildPlayersInRegionExpression : GuildExpression<Player>(Player::class.java) {

    companion object {
        init {
            registerExpression<GuildPlayersInRegionExpression, Player>(
                PROPERTY,
                "players in %guild%['s] region",
                "players in %string%['s] [guild] region",
                "players in %offlineplayer%['s] [guild] region",
                "players in %location%['s] [guild] region",
                "players in %block%['s] [guild] region",
                "players in %object%['s] [guild] region"
            )
        }
    }

    override fun get(event: Event): Array<Player> {
        return event.getGuild(guildExpression).toStream()
            .flatMap { it.getPlayersInGuildRegion().stream() }
            .collect(Collectors.toSet())
            .toTypedArray()
    }

    override fun isSingle(): Boolean {
        return false
    }


}
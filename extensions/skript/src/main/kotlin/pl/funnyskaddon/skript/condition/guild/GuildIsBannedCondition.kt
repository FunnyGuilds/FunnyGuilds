package pl.funnyskaddon.skript.condition.guild

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import net.dzikoysk.funnyguilds.guild.Guild
import org.bukkit.event.Event
import pl.funnyskaddon.skript.condition.GuildCondition
import pl.funnyskaddon.skript.registration.Registration.registerCondition
import pl.funnyskaddon.skript.getGuild
@Name("Guild Is Banned")
@Since("2.0.0")
@Description("Checks if the guild is banned")
@Examples(
    "if guild \"AC4U\" is banned:",
    "if guild \"AC4U\" is not banned:"
)
class GuildIsBannedCondition : GuildCondition(7) {

    companion object {
        init {
            registerCondition<GuildIsBannedCondition>(
                "%guild% is banned",
                "%string% [guild] is banned",
                "%offlineplayer%['s] guild is banned",
                "guild at %location% is banned",
                "guild at %block% is banned",
                "%object%['s] guild is banned",
                "%guild% is(n't| not) banned",
                "%string% [guild] is(n't| not) banned",
                "%offlineplayer%['s] guild is(n't| not) banned",
                "guild at %location% is(n't| not) banned",
                "guild at %block% is(n't| not) banned",
                "%object%['s] guild is(n't| not) banned",
            )
        }
    }

    override fun check(event: Event): Boolean {
        return event.getGuild(guildExpression)
            .map(Guild::isBanned)
            .orElse(false)
            .xor(isNegated)
    }

}
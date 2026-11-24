package pl.funnyskaddon.skript.expression.guild

import pl.funnyskaddon.skript.registration.Registration.PROPERTY
import pl.funnyskaddon.skript.registration.Registration.registerExpression
import pl.funnyskaddon.extension.orNull

import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.util.Kleenean
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.guild.Guild
import org.bukkit.event.Event
import pl.funnyskaddon.skript.getValue
import pl.funnyskaddon.skript.expression.FunnyExpression

@Name("Guild Name From Tag")
@Since("2.0.0")
@Description("Returns the name of a guild by its tag")
@Examples(
    "send \"%guild name from tag \"AD4U\"%\"",
    "set {_name} to guild name with tag \"AD4U\""
)
class GuildNameFromTagExpression : FunnyExpression<String>(String::class.java) {

    private lateinit var tagExpression: Expression<String>

    companion object {
        init {
            registerExpression<GuildNameFromTagExpression, String>(
                PROPERTY,
                "guild name from tag %string%",
                "guild name with tag %string%"
            )
        }
    }

    override fun init(
        expression: Array<Expression<*>>,
        matchedPattern: Int,
        isDelayed: Kleenean,
        parseResult: SkriptParser.ParseResult
    ): Boolean {
        tagExpression = expression[0] as Expression<String>
        return true
    }

    override fun get(event: Event): Array<String>? {
        return event.getValue(tagExpression)
            .flatMap(FunnyGuilds.getInstance().guildManager::findByTag)
            .map(Guild::getName)
            .map { value -> arrayOf(value) }
            .orNull()
    }

    override fun isSingle(): Boolean {
        return true
    }



}
package pl.funnyskaddon.skript

import ch.njol.skript.lang.Expression
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.user.User
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.Event
import java.util.Optional
import pl.funnyskaddon.extension.getGuild

fun <T> Event.getValue(expression: Expression<T>?): Optional<T & Any> {
    if (expression == null) {
        return Optional.empty()
    }
    return Optional.ofNullable(expression.getSingle(this))
}

fun Event.getOfflinePlayer(expression: Expression<OfflinePlayer>?): Optional<OfflinePlayer> {
    if (expression == null) {
        return Optional.empty()
    }
    return Optional.ofNullable((expression.getSingle(this)))
}

fun Event.getPlayer(expression: Expression<OfflinePlayer>?): Optional<Player> {
    if (expression == null) {
        return Optional.empty()
    }
    return getOfflinePlayer(expression)
        .map { it.player }
}

fun Event.getUser(expression: Expression<OfflinePlayer>?): Optional<User> {
    if (expression == null) {
        return Optional.empty()
    }
    return getOfflinePlayer(expression)
        .flatMap(FunnyGuilds.getInstance().userManager::findByPlayer)
}

fun Event.getGuild(expression: Expression<*>?): Optional<Guild> {
    if (expression == null) {
        return Optional.empty()
    }
    return expression.getSingle(this)?.getGuild() ?: Optional.empty()
}
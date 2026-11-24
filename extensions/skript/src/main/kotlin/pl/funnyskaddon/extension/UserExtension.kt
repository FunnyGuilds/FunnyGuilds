package pl.funnyskaddon.extension

import net.dzikoysk.funnyguilds.user.User
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.Optional

fun User.getOfflinePlayer(): Optional<OfflinePlayer> {
    return Optional.ofNullable(Bukkit.getOfflinePlayer(this.uuid))
}

fun User.getPlayerOption(): Optional<Player> {
    return Optional.ofNullable(Bukkit.getPlayer(this.uuid))
}
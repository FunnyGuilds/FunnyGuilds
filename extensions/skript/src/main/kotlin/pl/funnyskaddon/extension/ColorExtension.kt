package pl.funnyskaddon.extension

import net.md_5.bungee.api.ChatColor
import org.bukkit.command.CommandSender

fun String.color(): String {
    return ChatColor.translateAlternateColorCodes('&', this)
}

fun CommandSender.sendColorMessage(message: String) {
    this.sendMessage(message.color())
}

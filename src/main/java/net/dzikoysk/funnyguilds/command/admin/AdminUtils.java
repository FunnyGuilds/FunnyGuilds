package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

final class AdminUtils {

    private AdminUtils() {}

    public static @Nullable User getAdminUser(CommandSender sender) {
        return (sender instanceof Player)
                ? User.get(sender.getName())
                : null;
    }

    public static EventCause getCause(@Nullable User admin) {
        return admin == null
                ? EventCause.CONSOLE
                : EventCause.ADMIN;
    }

}

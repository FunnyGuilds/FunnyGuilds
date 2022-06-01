package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

final class AdminUtils {

    private AdminUtils() {
    }

    public static @Nullable User getAdminUser(CommandSender sender) {
        return (sender instanceof Player)
                ? FunnyGuilds.getInstance().getUserManager().findByName(sender.getName()).orNull()
                : null;
    }

    public static EventCause getCause(@Nullable User admin) {
        return admin == null
                ? EventCause.CONSOLE
                : EventCause.ADMIN;
    }

}

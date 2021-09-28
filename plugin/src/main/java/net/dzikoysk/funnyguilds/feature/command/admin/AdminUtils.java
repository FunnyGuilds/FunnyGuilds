package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

final class AdminUtils extends AbstractFunnyCommand {

    private AdminUtils() {}

    public static @Nullable User getAdminUser(CommandSender sender) {
        return (sender instanceof Player)
                ? UserUtils.get(sender.getName())
                : null;
    }

    public static EventCause getCause(@Nullable User admin) {
        return admin == null
                ? EventCause.CONSOLE
                : EventCause.ADMIN;
    }

}

package net.dzikoysk.funnyguilds.feature.security;

import java.util.Map;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.feature.security.cheat.CheatType;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.peridot.yetanothermessageslibrary.replace.Replaceable;
import pl.peridot.yetanothermessageslibrary.replace.replacement.Replacement;

public final class SecurityUtils {

    private static final double COMPENSATION_RATIO = 0.0056;

    private SecurityUtils() {
    }

    public static double compensationMs(double millisecond) {
        return millisecond * COMPENSATION_RATIO;
    }

    public static void sendToOperator(Player player, CheatType cheatType, Replaceable... noteReplacements) {
        MessageService messageService = FunnyGuilds.getInstance().getMessageService();

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{PLAYER}", player.getName())
                .register("{CHEAT}", cheatType.getName());

        FunnyGuilds.getInstance().getMessageService().getMessage(config -> config.securitySystemInfo)
                .broadcast()
                .with(formatter)
                .with(
                        CommandSender.class,
                        receiver -> Replacement.of("{NOTE}", messageService.get(receiver, cheatType.getNoteSupplier(), noteReplacements))
                )
                .permission("funnyguilds.admin")
                .send();
    }

    public static void addViolationLevel(User user) {
        Map<User, Integer> playersViolationLevel = SecuritySystem.getPlayersViolationLevel();
        playersViolationLevel.put(user, playersViolationLevel.getOrDefault(user, 0) + 1);
        Bukkit.getScheduler().runTaskLater(FunnyGuilds.getInstance(), () -> playersViolationLevel.remove(user), 18000);
    }

    public static boolean isBlocked(User user) {
        return SecuritySystem.getPlayersViolationLevel().getOrDefault(user, 0) > 1;
    }

}

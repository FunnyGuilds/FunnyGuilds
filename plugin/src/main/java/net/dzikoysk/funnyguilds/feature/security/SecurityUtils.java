package net.dzikoysk.funnyguilds.feature.security;

import com.google.common.cache.Cache;
import dev.peri.yetanothermessageslibrary.replace.Replaceable;
import dev.peri.yetanothermessageslibrary.replace.replacement.Replacement;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.feature.security.cheat.CheatType;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class SecurityUtils {

    private static final double COMPENSATION_RATIO = 0.0056;

    private SecurityUtils() {
    }

    public static double compensationMs(double millisecond) {
        return millisecond * COMPENSATION_RATIO;
    }

    public static void sendToOperator(Player player, CheatType cheatType, Replaceable... noteReplacements) {
        MessageService messageService = FunnyGuilds.getInstance().getMessageService();

        FunnyGuilds.getInstance().getMessageService().getMessage(config -> config.securitySystemInfo)
                .all()
                .permission("funnyguilds.admin")
                .with("{PLAYER}", player.getName())
                .with("{CHEAT}", cheatType.getName())
                .with(
                        CommandSender.class,
                        receiver -> Replacement.component("{NOTE}", messageService.getComponent(receiver, cheatType.getNoteSupplier(), noteReplacements))
                )
                .send();
    }

    public static void addViolationLevel(User user) {
        Cache<User, Integer> playersViolationLevel = SecuritySystem.getPlayersViolationLevel();
        playersViolationLevel.put(user, playersViolationLevel.asMap().getOrDefault(user, 0) + 1);
    }

    public static boolean isBlocked(User user) {
        return SecuritySystem.getPlayersViolationLevel().asMap().getOrDefault(user, 0) >= FunnyGuilds.getInstance().getPluginConfiguration().securitySystem.maxViolations;
    }

}

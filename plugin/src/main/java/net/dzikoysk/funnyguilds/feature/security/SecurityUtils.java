package net.dzikoysk.funnyguilds.feature.security;

import java.util.Map;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class SecurityUtils {

    private static final double COMPENSATION_RATIO = 0.0056;

    private SecurityUtils() {
    }

    public static double compensationMs(double millisecond) {
        return millisecond * COMPENSATION_RATIO;
    }

    public static void sendToOperator(Player player, String cheat, String note) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
        String message = messages.securitySystemPrefix + messages.securitySystemInfo;
        String messageNote = messages.securitySystemPrefix + messages.securitySystemNote;

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{PLAYER}", player.getName())
                .register("{CHEAT}", cheat)
                .register("{NOTE}", note);

        Bukkit.broadcast(formatter.format(message), "funnyguilds.admin");
        Bukkit.broadcast(formatter.format(messageNote), "funnyguilds.admin");
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

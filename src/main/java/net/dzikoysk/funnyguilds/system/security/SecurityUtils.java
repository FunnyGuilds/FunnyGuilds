package net.dzikoysk.funnyguilds.system.security;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class SecurityUtils {

    private static final double COMPENSATION_RATIO = 0.0056;

    public static void sendToOperator(Player player, String cheat, String note) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
        String message = messages.securitySystemPrefix + messages.securitySystemInfo;
        String messageNote = messages.securitySystemPrefix + messages.securitySystemNote;

        message = StringUtils.replace(message, "{PLAYER}", player.getName());
        message = StringUtils.replace(message, "{CHEAT}", cheat);
        messageNote = StringUtils.replace(messageNote, "{NOTE}", note);

        Bukkit.broadcast(ChatUtils.colored(message), "funnyguilds.admin");
        Bukkit.broadcast(ChatUtils.colored(messageNote), "funnyguilds.admin");
    }

    public static double compensationMs(double millisecond) {
        return millisecond * COMPENSATION_RATIO;
    }

}

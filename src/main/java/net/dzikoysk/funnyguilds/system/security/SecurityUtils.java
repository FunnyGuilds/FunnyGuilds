package net.dzikoysk.funnyguilds.system.security;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

public final class SecurityUtils {

    private static final String SECURITY_TAG = "&c!!!&4[FunnyGuilds]&c!!! ";

    private SecurityUtils() {}

    public static String getSecurityTag() {
        return SECURITY_TAG;
    }

    public static String getBustedMessage(Player player, CheatType cheat) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
        String message = messages.SecuritySystemInfo;

        message = StringUtils.replace(message, "{PLAYER}", player.getName());
        message = StringUtils.replace(message, "{CHEAT}", cheat.getName());

        return ChatUtils.colored(messages.SecuritySystemPrefix + message);
    }

    public static String getNoteMessage(String note) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
        String message = messages.SecuritySystemNote;

        message = StringUtils.replace(message, "{NOTE}", note);

        return ChatUtils.colored(messages.SecuritySystemPrefix + message);
    }

}

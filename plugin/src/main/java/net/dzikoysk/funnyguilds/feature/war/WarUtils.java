package net.dzikoysk.funnyguilds.feature.war;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import org.apache.commons.lang3.StringUtils;

public final class WarUtils {

    private WarUtils() {}

    @Deprecated // TODO: to remove
    public static String getMessage(Message type, Object... values) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
        String message;

        switch (type) {
            case NO_HAS_GUILD:
                message = messages.warHasNotGuild;
                break;
            case ALLY:
                message = messages.warAlly;
                break;
            case WAIT:
                message = messages.warWait;
                message = StringUtils.replace(message, "{TIME}", TimeUtils.getDurationBreakdown((long) values[0]));
                break;
            case ATTACKER:
                message = messages.warAttacker;
                message = StringUtils.replace(message, "{ATTACKED}", ((Guild) values[0]).getTag());
                break;
            case ATTACKED:
                message = messages.warAttacked;
                message = StringUtils.replace(message, "{ATTACKER}", ((Guild) values[0]).getTag());
                break;
            case DISABLED:
                message = messages.warDisabled;
                break;
            default:
                throw new IllegalArgumentException("Unknown message type " + type);
        }

        return message;
    }

    @Deprecated // TODO: to remove
    public enum Message {
        NO_HAS_GUILD,
        ALLY,
        WAIT,
        ATTACKER,
        ATTACKED,
        DISABLED
    }

    public static String getWinMessage(Guild conqueror, Guild loser) {
        return FunnyGuilds.getInstance().getMessageConfiguration().warWin
                .replace("{WINNER}", conqueror.getTag())
                .replace("{LOSER}", loser.getTag());
    }

    public static String getLoseMessage(Guild conqueror, Guild loser) {
        return FunnyGuilds.getInstance().getMessageConfiguration().warLose
                .replace("{WINNER}", conqueror.getTag())
                .replace("{LOSER}", loser.getTag());
    }

    public static String getBroadcastMessage(Guild conqueror, Guild loser) {
        return FunnyGuilds.getInstance().getMessageConfiguration().broadcastWar
                .replace("{WINNER}", conqueror.getTag())
                .replace("{LOSER}", loser.getTag());
    }

}

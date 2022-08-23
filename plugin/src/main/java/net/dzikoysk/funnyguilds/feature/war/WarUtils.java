package net.dzikoysk.funnyguilds.feature.war;

import java.time.Duration;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.TimeUtils;

public final class WarUtils {

    private WarUtils() {
    }

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
                message = FunnyFormatter.format(messages.warWait, "{TIME}", TimeUtils.formatTime((Duration) values[0]));
                break;
            case ATTACKER:
                message = FunnyFormatter.format(messages.warAttacker, "{ATTACKED}", ((Guild) values[0]).getTag());
                break;
            case ATTACKED:
                message = FunnyFormatter.format(messages.warAttacked, "{ATTACKER}", ((Guild) values[0]).getTag());
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
        return formatMessage(FunnyGuilds.getInstance().getMessageConfiguration().warWin, conqueror, loser);
    }

    public static String getLoseMessage(Guild conqueror, Guild loser) {
        return formatMessage(FunnyGuilds.getInstance().getMessageConfiguration().warLose, conqueror, loser);
    }

    public static String getBroadcastMessage(Guild conqueror, Guild loser) {
        return formatMessage(FunnyGuilds.getInstance().getMessageConfiguration().broadcastWar, conqueror, loser);
    }

    private static String formatMessage(String message, Guild conqueror, Guild loser) {
        FunnyFormatter formatter = new FunnyFormatter()
                .register("{WINNER}", conqueror.getTag())
                .register("{LOSER}", loser.getTag());

        return formatter.format(message);
    }

}

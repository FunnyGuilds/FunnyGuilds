package net.dzikoysk.funnyguilds.nms.api.message;

import java.util.Collection;
import org.bukkit.entity.Player;

public interface MessageAccessor {

    void sendTitleMessage(TitleMessage titleMessage, Player... players);

    default void sendTitleMessage(TitleMessage titleMessage, Collection<? extends Player> players) {
        this.sendTitleMessage(titleMessage, players.toArray(new Player[0]));
    }

    void sendActionBarMessage(String text, Player... players);

    default void sendActionBarMessage(String text, Collection<? extends Player> players) {
        this.sendActionBarMessage(text, players.toArray(new Player[0]));
    }

}

package net.dzikoysk.funnyguilds.nms.api.message;

import org.bukkit.entity.Player;

import java.util.Collection;

public interface MessageAccessor {
    void sendTitleMessage(TitleMessage titleMessage, Player... players);

    void sendTitleMessage(TitleMessage titleMessage, Collection<? extends Player> players);

    void sendActionBarMessage(String text, Player... players);

    void sendActionBarMessage(String text, Collection<? extends Player> players);


}

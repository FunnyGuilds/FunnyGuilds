package net.dzikoysk.funnyguilds.nms.api.playerlist;

import java.util.Set;
import org.bukkit.entity.Player;

public interface PlayerList {

    void send(Player player, String[] playerListCells, String header, String footer, SkinTexture[] cellTextures,
              int ping, Set<Integer> forceUpdateSlots);

}

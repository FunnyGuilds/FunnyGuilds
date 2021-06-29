package net.dzikoysk.funnyguilds.nms.v1_17R1.playerlist;

import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerList;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListAccessor;
import org.bukkit.entity.Player;

public class V1_17R1PlayerListAccessor implements PlayerListAccessor {

    @Override
    public PlayerList createPlayerList(Player player, int cellCount) {
        return new V1_17R1PlayerList(player, cellCount);
    }

}

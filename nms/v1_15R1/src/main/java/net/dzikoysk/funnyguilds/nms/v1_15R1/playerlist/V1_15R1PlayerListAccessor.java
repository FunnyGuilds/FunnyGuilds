package net.dzikoysk.funnyguilds.nms.v1_15R1.playerlist;

import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerList;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListAccessor;

public class V1_15R1PlayerListAccessor implements PlayerListAccessor {

    @Override
    public PlayerList createPlayerList(int cellCount) {
        return new V1_15R1PlayerList(cellCount);
    }
}

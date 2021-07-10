package net.dzikoysk.funnyguilds.nms.v1_11R1.playerlist;

import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerList;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListAccessor;

public class V1_11R1PlayerListAccessor implements PlayerListAccessor {

    @Override
    public PlayerList createPlayerList(int cellCount) {
        return new V1_11R1PlayerList(cellCount);
    }
}

package net.dzikoysk.funnyguilds.nms.v1_20R3.playerlist;

import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerList;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListAccessor;

public class V1_20R3PlayerListAccessor implements PlayerListAccessor {

    @Override
    public PlayerList createPlayerList(int cellCount) {
        return new V1_20R3PlayerList(cellCount);
    }

}

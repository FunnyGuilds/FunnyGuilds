package net.dzikoysk.funnyguilds.nms.v1_21R4;

import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListAccessor;
import net.dzikoysk.funnyguilds.nms.v1_21R1.V1_21R1NmsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_21R4.playerlist.V1_21R4PlayerList;

public class V1_21R4NmsAccessor extends V1_21R1NmsAccessor {

    @Override
    public PlayerListAccessor getPlayerListAccessor() {
        return V1_21R4PlayerList::new;
    }
}

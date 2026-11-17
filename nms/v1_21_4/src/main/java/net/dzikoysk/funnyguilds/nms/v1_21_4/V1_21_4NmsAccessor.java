package net.dzikoysk.funnyguilds.nms.v1_21_4;

import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListAccessor;
import net.dzikoysk.funnyguilds.nms.v1_21.V1_21NmsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_21_4.playerlist.V1_21_4PlayerList;

public class V1_21_4NmsAccessor extends V1_21NmsAccessor {

    @Override
    public PlayerListAccessor getPlayerListAccessor() {
        return V1_21_4PlayerList::new;
    }
}

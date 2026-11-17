package net.dzikoysk.funnyguilds.nms.v1_21_2;

import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListAccessor;
import net.dzikoysk.funnyguilds.nms.v1_21.V1_21NmsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_21_2.playerlist.V1_21_2PlayerList;

public class V1_21_2NmsAccessor extends V1_21NmsAccessor {

    @Override
    public PlayerListAccessor getPlayerListAccessor() {
        return V1_21_2PlayerList::new;
    }
}

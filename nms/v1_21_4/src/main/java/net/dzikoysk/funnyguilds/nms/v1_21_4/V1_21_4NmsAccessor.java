package net.dzikoysk.funnyguilds.nms.v1_21_4;

import net.dzikoysk.funnyguilds.nms.api.entity.EntityAccessor;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListAccessor;
import net.dzikoysk.funnyguilds.nms.v1_21R1.V1_21R1NmsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_21_4.entity.V1_21_4EntityAccessor;
import net.dzikoysk.funnyguilds.nms.v1_21_4.playerlist.V1_21_4PlayerList;

public class V1_21_4NmsAccessor extends V1_21R1NmsAccessor {

    @Override
    public EntityAccessor getEntityAccessor() {
        return new V1_21_4EntityAccessor();
    }

    @Override
    public PlayerListAccessor getPlayerListAccessor() {
        return V1_21_4PlayerList::new;
    }
}

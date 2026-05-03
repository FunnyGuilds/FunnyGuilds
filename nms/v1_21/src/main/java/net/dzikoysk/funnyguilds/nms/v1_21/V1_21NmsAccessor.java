package net.dzikoysk.funnyguilds.nms.v1_21;

import net.dzikoysk.funnyguilds.nms.api.NmsAccessor;
import net.dzikoysk.funnyguilds.nms.api.entity.EntityAccessor;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketAccessor;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListAccessor;
import net.dzikoysk.funnyguilds.nms.api.statistics.StatisticsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_21.entity.V1_21EntityAccessor;
import net.dzikoysk.funnyguilds.nms.v1_21.packet.V1_21PacketAccessor;
import net.dzikoysk.funnyguilds.nms.v1_21.playerlist.V1_21PlayerList;
import net.dzikoysk.funnyguilds.nms.v1_21.statistics.V1_21StatisticsAccessor;

public class V1_21NmsAccessor implements NmsAccessor {

    @Override
    public PacketAccessor getPacketAccessor() {
        return new V1_21PacketAccessor();
    }

    @Override
    public PlayerListAccessor getPlayerListAccessor() {
        return V1_21PlayerList::new;
    }

    @Override
    public StatisticsAccessor getStatisticsAccessor() {
        return new V1_21StatisticsAccessor();
    }

    @Override
    public EntityAccessor getEntityAccessor() {
        return new V1_21EntityAccessor();
    }

}

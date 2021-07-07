package net.dzikoysk.funnyguilds.nms.v1_15R1;

import net.dzikoysk.funnyguilds.nms.api.NmsAccessor;
import net.dzikoysk.funnyguilds.nms.api.entity.EntityAccessor;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketAccessor;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListAccessor;
import net.dzikoysk.funnyguilds.nms.api.statistics.StatisticsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_15R1.packet.V1_15R1PacketAccessor;

public class V1_15R1NmsAccessor implements NmsAccessor {
    @Override
    public PacketAccessor getPacketAccessor() {
        return new V1_15R1PacketAccessor();
    }

    @Override
    public PlayerListAccessor getPlayerListAccessor() {
        return null;
    }

    @Override
    public StatisticsAccessor getStatisticsAccessor() {
        return null;
    }

    @Override
    public EntityAccessor getEntityAccessor() {
        return null;
    }
}

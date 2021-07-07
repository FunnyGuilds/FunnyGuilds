package net.dzikoysk.funnyguilds.nms.v1_13R2;

import net.dzikoysk.funnyguilds.nms.api.NmsAccessor;
import net.dzikoysk.funnyguilds.nms.api.entity.EntityAccessor;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketAccessor;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListAccessor;
import net.dzikoysk.funnyguilds.nms.api.statistics.StatisticsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_13R2.packet.V1_13R2PacketAccessor;

public class V1_13R2NmsAccessor implements NmsAccessor {
    @Override
    public PacketAccessor getPacketAccessor() {
        return new V1_13R2PacketAccessor();
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

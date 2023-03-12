package net.dzikoysk.funnyguilds.nms.v1_19R2;

import net.dzikoysk.funnyguilds.nms.api.NmsAccessor;
import net.dzikoysk.funnyguilds.nms.api.entity.EntityAccessor;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketAccessor;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListAccessor;
import net.dzikoysk.funnyguilds.nms.api.statistics.StatisticsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_19R2.entity.V1_19R2EntityAccessor;
import net.dzikoysk.funnyguilds.nms.v1_19R2.packet.V1_19R2PacketAccessor;
import net.dzikoysk.funnyguilds.nms.v1_19R2.playerlist.V1_19R2PlayerListAccessor;
import net.dzikoysk.funnyguilds.nms.v1_19R2.statistics.V1_19R2StatisticsAccessor;

public class V1_19R2NmsAccessor implements NmsAccessor {

    @Override
    public PacketAccessor getPacketAccessor() {
        return new V1_19R2PacketAccessor();
    }

    @Override
    public PlayerListAccessor getPlayerListAccessor() {
        return new V1_19R2PlayerListAccessor();
    }

    @Override
    public StatisticsAccessor getStatisticsAccessor() {
        return new V1_19R2StatisticsAccessor();
    }

    @Override
    public EntityAccessor getEntityAccessor() {
        return new V1_19R2EntityAccessor();
    }

}

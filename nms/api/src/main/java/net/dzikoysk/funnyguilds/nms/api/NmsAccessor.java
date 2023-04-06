package net.dzikoysk.funnyguilds.nms.api;

import net.dzikoysk.funnyguilds.nms.api.entity.EntityAccessor;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketAccessor;
import net.dzikoysk.funnyguilds.nms.api.statistics.StatisticsAccessor;

public interface NmsAccessor {

    PacketAccessor getPacketAccessor();

    StatisticsAccessor getStatisticsAccessor();

    EntityAccessor getEntityAccessor();

}

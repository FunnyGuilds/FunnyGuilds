package net.dzikoysk.funnyguilds.nms.v26_1_2;

import net.dzikoysk.funnyguilds.nms.api.NmsAccessor;
import net.dzikoysk.funnyguilds.nms.api.entity.EntityAccessor;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketAccessor;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListAccessor;
import net.dzikoysk.funnyguilds.nms.api.statistics.StatisticsAccessor;
import net.dzikoysk.funnyguilds.nms.v26_1_2.entity.V26_1_2EntityAccessor;
import net.dzikoysk.funnyguilds.nms.v26_1_2.packet.V26_1_2PacketAccessor;
import net.dzikoysk.funnyguilds.nms.v26_1_2.playerlist.V26_1_2PlayerList;
import net.dzikoysk.funnyguilds.nms.v26_1_2.statistics.V26_1_2StatisticsAccessor;

public class V26_1_2NmsAccessor implements NmsAccessor {

    @Override
    public PacketAccessor getPacketAccessor() {
        return new V26_1_2PacketAccessor();
    }

    @Override
    public PlayerListAccessor getPlayerListAccessor() {
        return V26_1_2PlayerList::new;
    }

    @Override
    public StatisticsAccessor getStatisticsAccessor() {
        return new V26_1_2StatisticsAccessor();
    }

    @Override
    public EntityAccessor getEntityAccessor() {
        return new V26_1_2EntityAccessor();
    }

}

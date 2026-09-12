package net.dzikoysk.funnyguilds.nms.v26_2;

import net.dzikoysk.funnyguilds.nms.api.packet.PacketAccessor;
import net.dzikoysk.funnyguilds.nms.v1_21_9.V1_21_9NmsAccessor;
import net.dzikoysk.funnyguilds.nms.v26_2.packet.V26_2PacketAccessor;

public class V26_2NmsAccessor extends V1_21_9NmsAccessor {

    @Override
    public PacketAccessor getPacketAccessor() {
        return new V26_2PacketAccessor();
    }

}

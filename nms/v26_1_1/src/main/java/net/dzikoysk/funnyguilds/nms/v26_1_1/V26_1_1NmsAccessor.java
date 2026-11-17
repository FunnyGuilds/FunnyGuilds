package net.dzikoysk.funnyguilds.nms.v26_1_1;

import net.dzikoysk.funnyguilds.nms.api.packet.PacketAccessor;
import net.dzikoysk.funnyguilds.nms.v1_21_9.V1_21_9NmsAccessor;
import net.dzikoysk.funnyguilds.nms.v26_1_1.packet.V26_1_1PacketAccessor;

public class V26_1_1NmsAccessor extends V1_21_9NmsAccessor {

    @Override
    public PacketAccessor getPacketAccessor() {
        return new V26_1_1PacketAccessor();
    }

}

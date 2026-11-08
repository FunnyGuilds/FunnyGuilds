package net.dzikoysk.funnyguilds.nms.v26_3;

import net.dzikoysk.funnyguilds.nms.api.packet.PacketAccessor;
import net.dzikoysk.funnyguilds.nms.v26_1_1.V26_1_1NmsAccessor;
import net.dzikoysk.funnyguilds.nms.v26_3.packet.V26_3PacketAccessor;

public class V26_3NmsAccessor extends V26_1_1NmsAccessor {

    @Override
    public PacketAccessor getPacketAccessor() {
        return new V26_3PacketAccessor();
    }

}

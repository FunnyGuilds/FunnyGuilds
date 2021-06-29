package net.dzikoysk.funnyguilds.nms.api;

import net.dzikoysk.funnyguilds.nms.api.packet.PacketRegistry;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListAccessor;

public interface NmsAccessor {
    PacketRegistry getPacketRegistry();

    PlayerListAccessor getPlayerListAccessor();
}

package net.dzikoysk.funnyguilds.nms.api.packet;

import io.netty.channel.ChannelHandler;

public interface FunnyGuildsOutboundChannelHandler {

    PacketSuppliersRegistry getPacketSuppliersRegistry();

}

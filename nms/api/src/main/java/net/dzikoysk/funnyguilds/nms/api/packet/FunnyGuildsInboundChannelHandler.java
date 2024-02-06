package net.dzikoysk.funnyguilds.nms.api.packet;

import io.netty.channel.ChannelHandler;

public interface FunnyGuildsInboundChannelHandler {

    PacketCallbacksRegistry getPacketCallbacksRegistry();

}

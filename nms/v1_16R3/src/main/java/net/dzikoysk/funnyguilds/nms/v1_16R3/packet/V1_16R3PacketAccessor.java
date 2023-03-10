package net.dzikoysk.funnyguilds.nms.v1_16R3.packet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsInboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsOutboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketAccessor;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class V1_16R3PacketAccessor implements PacketAccessor {

    private final GenericInboundChannelHandlerInstaller<V1_16R3FunnyGuildsInboundChannelHandler> inboundChannelHandlerInstaller =
            new GenericInboundChannelHandlerInstaller<>(V1_16R3FunnyGuildsInboundChannelHandler::new);
    private final GenericOutboundChannelHandlerInstaller<V1_16R3FunnyGuildsOutboundChannelHandler> outboundChannelHandlerInstaller =
            new GenericOutboundChannelHandlerInstaller<>(V1_16R3FunnyGuildsOutboundChannelHandler::new);

    @Override
    public FunnyGuildsInboundChannelHandler getOrInstallInboundChannelHandler(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        Channel channel = entityPlayer.playerConnection.networkManager.channel;
        ChannelPipeline pipeline = channel.pipeline();

        return this.inboundChannelHandlerInstaller.installChannelHandlerInPipeline(pipeline);
    }

    @Override
    public FunnyGuildsOutboundChannelHandler getOrInstallOutboundChannelHandler(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        Channel channel = entityPlayer.playerConnection.networkManager.channel;
        ChannelPipeline pipeline = channel.pipeline();

        return this.outboundChannelHandlerInstaller.installChannelHandlerOutPipeline(pipeline);
    }

}

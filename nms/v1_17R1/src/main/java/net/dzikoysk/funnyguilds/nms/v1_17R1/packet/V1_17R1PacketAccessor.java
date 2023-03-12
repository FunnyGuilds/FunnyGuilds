package net.dzikoysk.funnyguilds.nms.v1_17R1.packet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsInboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsOutboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketAccessor;
import net.dzikoysk.funnyguilds.nms.v1_16R3.packet.GenericInboundChannelHandlerInstaller;
import net.dzikoysk.funnyguilds.nms.v1_16R3.packet.GenericOutboundChannelHandlerInstaller;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class V1_17R1PacketAccessor implements PacketAccessor {

    private final GenericInboundChannelHandlerInstaller<V1_17R1FunnyGuildsInboundChannelHandler> inboundChannelHandlerInstaller =
            new GenericInboundChannelHandlerInstaller<>(V1_17R1FunnyGuildsInboundChannelHandler::new);
    private final GenericOutboundChannelHandlerInstaller<V1_17R1FunnyGuildsOutboundChannelHandler> outboundChannelHandlerInstaller =
            new GenericOutboundChannelHandlerInstaller<>(V1_17R1FunnyGuildsOutboundChannelHandler::new);

    @Override
    public FunnyGuildsInboundChannelHandler getOrInstallInboundChannelHandler(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        // entityPlayer.playerConnection.networkManager.channel
        Channel channel = entityPlayer.b.a.k;
        ChannelPipeline pipeline = channel.pipeline();

        return this.inboundChannelHandlerInstaller.installChannelHandlerInPipeline(pipeline);
    }

    @Override
    public FunnyGuildsOutboundChannelHandler getOrInstallOutboundChannelHandler(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        // entityPlayer.playerConnection.networkManager.channel
        Channel channel = entityPlayer.b.a.k;
        ChannelPipeline pipeline = channel.pipeline();

        return this.outboundChannelHandlerInstaller.installChannelHandlerOutPipeline(pipeline);
    }

}

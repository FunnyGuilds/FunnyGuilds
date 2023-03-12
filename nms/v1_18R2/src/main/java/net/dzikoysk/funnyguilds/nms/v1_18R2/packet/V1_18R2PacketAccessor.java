package net.dzikoysk.funnyguilds.nms.v1_18R2.packet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsInboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsOutboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketAccessor;
import net.dzikoysk.funnyguilds.nms.v1_16R3.packet.GenericInboundChannelHandlerInstaller;
import net.dzikoysk.funnyguilds.nms.v1_16R3.packet.GenericOutboundChannelHandlerInstaller;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class V1_18R2PacketAccessor implements PacketAccessor {

    private final GenericInboundChannelHandlerInstaller<V1_18R2FunnyGuildsInboundChannelHandler> inboundChannelHandlerInstaller =
            new GenericInboundChannelHandlerInstaller<>(V1_18R2FunnyGuildsInboundChannelHandler::new);
    private final GenericOutboundChannelHandlerInstaller<V1_18R2FunnyGuildsOutboundChannelHandler> outboundChannelHandlerInstaller =
            new GenericOutboundChannelHandlerInstaller<>(V1_18R2FunnyGuildsOutboundChannelHandler::new);

    @Override
    public FunnyGuildsInboundChannelHandler getOrInstallInboundChannelHandler(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        // entityPlayer.playerConnection.networkManager.channel
        Channel channel = entityPlayer.b.a.m;
        ChannelPipeline pipeline = channel.pipeline();

        return this.inboundChannelHandlerInstaller.installChannelHandlerInPipeline(pipeline);
    }

    @Override
    public FunnyGuildsOutboundChannelHandler getOrInstallOutboundChannelHandler(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        // entityPlayer.playerConnection.networkManager.channel
        Channel channel = entityPlayer.b.a.m;
        ChannelPipeline pipeline = channel.pipeline();

        return this.outboundChannelHandlerInstaller.installChannelHandlerOutPipeline(pipeline);
    }

}

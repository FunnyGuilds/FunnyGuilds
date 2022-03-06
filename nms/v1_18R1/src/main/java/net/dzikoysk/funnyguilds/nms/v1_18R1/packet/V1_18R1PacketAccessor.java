package net.dzikoysk.funnyguilds.nms.v1_18R1.packet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsInboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsOutboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketAccessor;
import net.dzikoysk.funnyguilds.nms.v1_8R3.packet.GenericInboundChannelHandlerInstaller;
import net.dzikoysk.funnyguilds.nms.v1_8R3.packet.GenericOutboundChannelHandlerInstaller;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class V1_18R1PacketAccessor implements PacketAccessor {

    private final GenericInboundChannelHandlerInstaller inboundChannelHandlerInstaller = new GenericInboundChannelHandlerInstaller(V1_18R1FunnyGuildsInboundChannelHandler::new);
    private final GenericOutboundChannelHandlerInstaller outboundChannelHandlerInstaller = new GenericOutboundChannelHandlerInstaller(V1_18R1FunnyGuildsOutboundChannelHandler::new);

    @Override
    public FunnyGuildsInboundChannelHandler getOrInstallInboundChannelHandler(Player player) {
        final EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        // entityPlayer.playerConnection.networkManager.channel
        final Channel channel = entityPlayer.b.a.k;
        final ChannelPipeline pipeline = channel.pipeline();

        return this.inboundChannelHandlerInstaller.installChannelHandlerPipeline(pipeline);
    }

    @Override
    public FunnyGuildsOutboundChannelHandler getOrInstallOutboundChannelHandler(Player player) {
        final EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        // entityPlayer.playerConnection.networkManager.channel
        final Channel channel = entityPlayer.b.a.k;
        final ChannelPipeline pipeline = channel.pipeline();

        return this.outboundChannelHandlerInstaller.installChannelHandlerPipeline(pipeline);
    }

}

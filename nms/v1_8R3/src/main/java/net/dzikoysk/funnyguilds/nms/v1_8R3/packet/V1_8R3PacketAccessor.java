package net.dzikoysk.funnyguilds.nms.v1_8R3.packet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsInboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsOutboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketAccessor;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class V1_8R3PacketAccessor implements PacketAccessor {

    private final GenericInboundChannelHandlerInstaller inboundChannelHandlerInstaller = new GenericInboundChannelHandlerInstaller(V1_8R3FunnyGuildsInboundChannelHandler::new);
    private final GenericOutboundChannelHandlerInstaller outboundChannelHandlerInstaller = new GenericOutboundChannelHandlerInstaller(V1_8R3FunnyGuildsOutboundChannelHandler::new);

    @Override
    public FunnyGuildsInboundChannelHandler getOrInstallInboundChannelHandler(Player player) {
        final EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        final Channel channel = entityPlayer.playerConnection.networkManager.channel;
        final ChannelPipeline pipeline = channel.pipeline();

        return this.inboundChannelHandlerInstaller.installChannelHandlerPipeline(pipeline);
    }

    @Override
    public FunnyGuildsOutboundChannelHandler getOrInstallOutboundChannelHandler(Player player) {
        final EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        final Channel channel = entityPlayer.playerConnection.networkManager.channel;
        final ChannelPipeline pipeline = channel.pipeline();

        return this.outboundChannelHandlerInstaller.installChannelHandlerPipeline(pipeline);
    }

}

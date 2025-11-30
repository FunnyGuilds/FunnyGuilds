package net.dzikoysk.funnyguilds.nms.v1_21.packet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.dzikoysk.funnyguilds.nms.api.network.GenericInboundChannelHandlerInstaller;
import net.dzikoysk.funnyguilds.nms.api.network.GenericOutboundChannelHandlerInstaller;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsInboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsOutboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketAccessor;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class V1_21PacketAccessor implements PacketAccessor {

    private final GenericInboundChannelHandlerInstaller<?> inboundChannelHandlerInstaller =
            new GenericInboundChannelHandlerInstaller<>(V1_21FunnyGuildsInboundChannelHandler::new);
    private final GenericOutboundChannelHandlerInstaller<?> outboundChannelHandlerInstaller =
            new GenericOutboundChannelHandlerInstaller<>(V1_21FunnyGuildsOutboundChannelHandler::new);

    @Override
    public FunnyGuildsInboundChannelHandler getOrInstallInboundChannelHandler(Player player) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();

        Channel channel = serverPlayer.connection.connection.channel;
        ChannelPipeline pipeline = channel.pipeline();

        return this.inboundChannelHandlerInstaller.installChannelHandlerInPipeline(pipeline);
    }

    @Override
    public FunnyGuildsOutboundChannelHandler getOrInstallOutboundChannelHandler(Player player) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();

        Channel channel = serverPlayer.connection.connection.channel;
        ChannelPipeline pipeline = channel.pipeline();

        return this.outboundChannelHandlerInstaller.installChannelHandlerOutPipeline(pipeline);
    }
}

package net.dzikoysk.funnyguilds.nms.v1_19R3.packet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import java.lang.reflect.Field;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsInboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsOutboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketAccessor;
import net.dzikoysk.funnyguilds.nms.v1_16R3.packet.GenericInboundChannelHandlerInstaller;
import net.dzikoysk.funnyguilds.nms.v1_16R3.packet.GenericOutboundChannelHandlerInstaller;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class V1_19R3PacketAccessor implements PacketAccessor {
    
    private final GenericInboundChannelHandlerInstaller<V1_19R3FunnyGuildsInboundChannelHandler> inboundChannelHandlerInstaller =
            new GenericInboundChannelHandlerInstaller<>(V1_19R3FunnyGuildsInboundChannelHandler::new);
    private final GenericOutboundChannelHandlerInstaller<V1_19R3FunnyGuildsOutboundChannelHandler> outboundChannelHandlerInstaller =
            new GenericOutboundChannelHandlerInstaller<>(V1_19R3FunnyGuildsOutboundChannelHandler::new);
    
    @Override
    public FunnyGuildsInboundChannelHandler getOrInstallInboundChannelHandler(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
    
        Channel channel = this.getNetworkManager(entityPlayer).m;
        ChannelPipeline pipeline = channel.pipeline();

        return this.inboundChannelHandlerInstaller.installChannelHandlerInPipeline(pipeline);
    }

    @Override
    public FunnyGuildsOutboundChannelHandler getOrInstallOutboundChannelHandler(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
    
        Channel channel = this.getNetworkManager(entityPlayer).m;
        ChannelPipeline pipeline = channel.pipeline();

        return this.outboundChannelHandlerInstaller.installChannelHandlerOutPipeline(pipeline);
    }
    
    private NetworkManager getNetworkManager(EntityPlayer entityPlayer) {
        Field networkManagerField;
        try {
            networkManagerField = entityPlayer.b.getClass().getDeclaredField("h");
            networkManagerField.setAccessible(true);
        } catch (NoSuchFieldException exception) {
            throw new RuntimeException("missing 'h' field in PlayerConnection", exception);
        }
    
        try {
            return (NetworkManager) networkManagerField.get(entityPlayer.b);
        } catch (IllegalAccessException exception) {
            throw new RuntimeException("Failed to initialise V1_19R3PacketAccessor", exception);
        }
    }

}
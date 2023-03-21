package net.dzikoysk.funnyguilds.nms.v1_19R3.packet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsInboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsOutboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketAccessor;
import net.dzikoysk.funnyguilds.nms.v1_16R3.packet.GenericInboundChannelHandlerInstaller;
import net.dzikoysk.funnyguilds.nms.v1_16R3.packet.GenericOutboundChannelHandlerInstaller;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class V1_19R3PacketAccessor implements PacketAccessor {
    
    private static final Field NETOWRK_MANAGER_FIELD;

    private final GenericInboundChannelHandlerInstaller<V1_19R3FunnyGuildsInboundChannelHandler> inboundChannelHandlerInstaller =
            new GenericInboundChannelHandlerInstaller<>(V1_19R3FunnyGuildsInboundChannelHandler::new);
    private final GenericOutboundChannelHandlerInstaller<V1_19R3FunnyGuildsOutboundChannelHandler> outboundChannelHandlerInstaller =
            new GenericOutboundChannelHandlerInstaller<>(V1_19R3FunnyGuildsOutboundChannelHandler::new);
    
    static {
        try {
            NETOWRK_MANAGER_FIELD = PlayerConnection.class.getDeclaredField("h");
            NETOWRK_MANAGER_FIELD.setAccessible(true);
        }
        catch (NoSuchFieldException exception) {
            throw new RuntimeException("Failed to initialise V1_19R3PacketAccessor", exception);
        }
    }
    
    @Override
    public FunnyGuildsInboundChannelHandler getOrInstallInboundChannelHandler(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        
        NetworkManager networkManager;
        try {
            networkManager = (NetworkManager) NETOWRK_MANAGER_FIELD.get(entityPlayer);
        } catch (IllegalAccessException exception) {
            throw new RuntimeException("Failed to initialise V1_19R3PacketAccessor", exception);
        }
        
        Channel channel = networkManager.m;
        ChannelPipeline pipeline = channel.pipeline();

        return this.inboundChannelHandlerInstaller.installChannelHandlerInPipeline(pipeline);
    }

    @Override
    public FunnyGuildsOutboundChannelHandler getOrInstallOutboundChannelHandler(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
    
        NetworkManager networkManager;
        try {
            networkManager = (NetworkManager) NETOWRK_MANAGER_FIELD.get(entityPlayer);
        } catch (IllegalAccessException exception) {
            throw new RuntimeException("Failed to initialise V1_19R3PacketAccessor", exception);
        }
    
        Channel channel = networkManager.m;
        ChannelPipeline pipeline = channel.pipeline();

        return this.outboundChannelHandlerInstaller.installChannelHandlerOutPipeline(pipeline);
    }

}
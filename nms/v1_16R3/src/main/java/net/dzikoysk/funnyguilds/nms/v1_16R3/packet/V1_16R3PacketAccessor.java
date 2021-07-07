package net.dzikoysk.funnyguilds.nms.v1_16R3.packet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketAccessor;
import net.dzikoysk.funnyguilds.nms.v1_8R3.packet.GenericChannelHandlerInstaller;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class V1_16R3PacketAccessor implements PacketAccessor {

    private final GenericChannelHandlerInstaller channelHandlerInstaller = new GenericChannelHandlerInstaller(V1_16R3FunnyGuildsChannelHandler::new);

    @Override
    public FunnyGuildsChannelHandler getOrInstallChannelHandler(Player player) {

        final EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        final Channel channel = entityPlayer.playerConnection.networkManager.channel;
        final ChannelPipeline pipeline = channel.pipeline();

        return this.channelHandlerInstaller.installChannelHandlerInPipeline(pipeline);
    }
}

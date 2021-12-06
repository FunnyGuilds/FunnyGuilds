package net.dzikoysk.funnyguilds.nms.v1_18R1.packet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketAccessor;
import net.dzikoysk.funnyguilds.nms.v1_8R3.packet.GenericChannelHandlerInstaller;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class V1_18PacketAccessor implements PacketAccessor {

    private final GenericChannelHandlerInstaller channelHandlerInstaller = new GenericChannelHandlerInstaller(net.dzikoysk.funnyguilds.nms.v1_18R1.packet.V1_18R1FunnyGuildsChannelHandler::new);

    @Override
    public FunnyGuildsChannelHandler getOrInstallChannelHandler(Player player) {

        final EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        // entityPlayer.playerConnection.networkManager.channel
        final Channel channel = entityPlayer.b.a.k;
        final ChannelPipeline pipeline = channel.pipeline();

        return this.channelHandlerInstaller.installChannelHandlerInPipeline(pipeline);
    }

}

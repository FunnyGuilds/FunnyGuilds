package net.dzikoysk.funnyguilds.nms.v1_17R1.packet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketAccessor;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class V1_17PacketAccessor implements PacketAccessor {
    private static final String FUNNY_GUILDS_HANDLER_ID = "FunnyGuilds";

    @Override
    public FunnyGuildsChannelHandler getOrInstallChannelHandler(Player player) {

        final EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        // entityPlayer.playerConnection.networkManager.channel
        final Channel channel = entityPlayer.b.a.k;
        final ChannelPipeline pipeline = channel.pipeline();

        final ChannelHandler oldChannelHandler = pipeline.get(FUNNY_GUILDS_HANDLER_ID);
        if (oldChannelHandler == null) {
            final V1_17R1FunnyGuildsChannelHandler newChannelHandler = new V1_17R1FunnyGuildsChannelHandler();
            pipeline.addBefore("packet_handler", FUNNY_GUILDS_HANDLER_ID, newChannelHandler);

            return newChannelHandler;
        }

        if (oldChannelHandler instanceof FunnyGuildsChannelHandler) {
            return (FunnyGuildsChannelHandler) oldChannelHandler;
        }

        // this case handles /reload
        final V1_17R1FunnyGuildsChannelHandler newChannelHandler = new V1_17R1FunnyGuildsChannelHandler();
        pipeline.replace(FUNNY_GUILDS_HANDLER_ID, FUNNY_GUILDS_HANDLER_ID, newChannelHandler);

        return newChannelHandler;
    }

}

package net.dzikoysk.funnyguilds.nms.v1_8R3.packet;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import java.util.function.Supplier;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsOutboundChannelHandler;

import static net.dzikoysk.funnyguilds.nms.api.packet.PacketAccessorConstants.FUNNY_GUILDS_OUT_HANDLER_ID;

public class GenericOutboundChannelHandlerInstaller<T extends ChannelHandler & FunnyGuildsOutboundChannelHandler> {

    private final Supplier<T> channelHandlerSupplier;

    public GenericOutboundChannelHandlerInstaller(Supplier<T> channelHandlerSupplier) {
        this.channelHandlerSupplier = channelHandlerSupplier;
    }

    public FunnyGuildsOutboundChannelHandler installChannelHandlerPipeline(ChannelPipeline pipeline) {
        final ChannelHandler oldChannelHandler = pipeline.get(FUNNY_GUILDS_OUT_HANDLER_ID);
        if (oldChannelHandler == null) {
            final T newChannelHandler = this.channelHandlerSupplier.get();
            pipeline.addBefore("packet_handler", FUNNY_GUILDS_OUT_HANDLER_ID, newChannelHandler);

            return newChannelHandler;
        }

        if (oldChannelHandler instanceof FunnyGuildsOutboundChannelHandler) {
            return (FunnyGuildsOutboundChannelHandler) oldChannelHandler;
        }

        // this case handles /reload
        final T newChannelHandler = this.channelHandlerSupplier.get();
        pipeline.replace(FUNNY_GUILDS_OUT_HANDLER_ID, FUNNY_GUILDS_OUT_HANDLER_ID, newChannelHandler);

        return newChannelHandler;
    }
}

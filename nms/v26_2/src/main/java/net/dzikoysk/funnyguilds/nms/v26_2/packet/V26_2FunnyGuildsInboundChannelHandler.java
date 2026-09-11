package net.dzikoysk.funnyguilds.nms.v26_2.packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsInboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketCallbacksRegistry;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.NotNull;

public class V26_2FunnyGuildsInboundChannelHandler extends ChannelInboundHandlerAdapter implements FunnyGuildsInboundChannelHandler {
    private final PacketCallbacksRegistry packetCallbacksRegistry = new PacketCallbacksRegistry();

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        if (msg instanceof ServerboundAttackPacket attackPacket) {
            this.packetCallbacksRegistry.handleAttackEntity(attackPacket.entityId(), true);
        } else if (msg instanceof ServerboundInteractPacket interactPacket) {
            boolean isMainHand = interactPacket.hand() == InteractionHand.MAIN_HAND;

            this.packetCallbacksRegistry.handleRightClickEntity(interactPacket.entityId(), isMainHand);
        }

        super.channelRead(ctx, msg);
    }

    @Override
    public PacketCallbacksRegistry getPacketCallbacksRegistry() {
        return this.packetCallbacksRegistry;
    }
}

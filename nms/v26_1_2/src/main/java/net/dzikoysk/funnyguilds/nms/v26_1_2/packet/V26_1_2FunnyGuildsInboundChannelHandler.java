package net.dzikoysk.funnyguilds.nms.v26_1_2.packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsInboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketCallbacksRegistry;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.NotNull;

public class V26_1_2FunnyGuildsInboundChannelHandler extends ChannelInboundHandlerAdapter implements FunnyGuildsInboundChannelHandler {
    private final PacketCallbacksRegistry packetCallbacksRegistry = new PacketCallbacksRegistry();

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        // As of Minecraft 26.1 ServerboundInteractPacket is a flat record instead of the old
        // Action/Handler dispatch. The action is encoded by which fields are present:
        //   attack       -> hand == null, location == null
        //   interact     -> hand != null, location == null
        //   interact-at  -> hand != null, location != null
        // A single right-click sends both an interact and an interact-at packet, so - matching the
        // previous behaviour - only interact-at (location != null) is treated as a right-click to
        // avoid firing the callback twice.
        if (msg instanceof ServerboundInteractPacket interactPacket) {
            int entityId = interactPacket.entityId();
            InteractionHand interactionHand = interactPacket.hand();

            if (interactionHand == null) {
                this.packetCallbacksRegistry.handleAttackEntity(entityId, true);
            }
            else if (interactPacket.location() != null) {
                boolean isMainHand = interactionHand == InteractionHand.MAIN_HAND;
                this.packetCallbacksRegistry.handleRightClickEntity(entityId, isMainHand);
            }
        }

        super.channelRead(ctx, msg);
    }

    @Override
    public PacketCallbacksRegistry getPacketCallbacksRegistry() {
        return this.packetCallbacksRegistry;
    }
}

package net.dzikoysk.funnyguilds.nms.v1_20R1.packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsInboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketCallbacksRegistry;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class V1_20R1FunnyGuildsInboundChannelHandler extends ChannelInboundHandlerAdapter implements FunnyGuildsInboundChannelHandler {

    private final PacketCallbacksRegistry packetCallbacksRegistry = new PacketCallbacksRegistry();

    // MC 1.20
    private static Method getActionType;
    private static Object attackActionType;

    // MC 1.20.1
    private static Method isAttack;

    static {
        try {
            isAttack = ServerboundInteractPacket.class.getDeclaredMethod("isAttack");
        } catch (NoSuchMethodException olderVersion) {
            try {
                getActionType = ServerboundInteractPacket.class.getDeclaredMethod("getActionType");

                Class<?> actionType = getActionType.getReturnType();
                Method enumValueOf = actionType.getMethod("valueOf", String.class);
                attackActionType = enumValueOf.invoke(null, "ATTACK");
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
                throw new RuntimeException("Failed to initialize v1_20R1 support", ex);
            }
        }
    }

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        if (msg instanceof ServerboundInteractPacket interactPacket) {
            int entityId = interactPacket.getEntityId();

            if (this.isAttack(interactPacket)) {
                this.packetCallbacksRegistry.handleAttackEntity(entityId, true);
            } else {
                InteractionHand interactionHand = getInteractionHand(interactPacket);

                if (interactionHand != null) {
                    boolean isMainHand = interactionHand == InteractionHand.MAIN_HAND;

                    this.packetCallbacksRegistry.handleRightClickEntity(entityId, isMainHand);
                }
            }
        }

        super.channelRead(ctx, msg);
    }

    @Override
    public PacketCallbacksRegistry getPacketCallbacksRegistry() {
        return this.packetCallbacksRegistry;
    }

    private @Nullable InteractionHand getInteractionHand(ServerboundInteractPacket interactPacket) {
        InteractionHand[] interactHand = new InteractionHand[1];

        interactPacket.dispatch(new ServerboundInteractPacket.Handler() {
            @Override
            public void onInteraction(InteractionHand interactionHand) {
                // no-op
            }

            @Override
            public void onInteraction(InteractionHand interactionHand, Vec3 vec3) {
                // interact at
                interactHand[0] = interactionHand;
            }

            @Override
            public void onAttack() {
                // no-op
            }
        });

        return interactHand[0];
    }

    private boolean isAttack(ServerboundInteractPacket interactPacket) {
        try {
            if (isAttack != null) {
                return (boolean) isAttack.invoke(interactPacket);
            } else if (getActionType != null) {
                return getActionType.invoke(interactPacket).equals(attackActionType);
            } else {
                throw new IllegalStateException("No packet action accessors initialized");
            }
        } catch (InvocationTargetException | IllegalAccessException ex) {
            throw new RuntimeException("Failed to determine interaction type", ex);
        }
    }
}

package net.dzikoysk.funnyguilds.nms.v1_17R1.packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsInboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketCallbacksRegistry;
import net.minecraft.network.protocol.game.PacketPlayInUseEntity;

public class V1_17R1FunnyGuildsInboundChannelHandler extends ChannelInboundHandlerAdapter implements FunnyGuildsInboundChannelHandler {
    private final PacketCallbacksRegistry packetCallbacksRegistry = new PacketCallbacksRegistry();

    private static final Field ENTITY_ID;
    private static final Field ACTION_TYPE;
    private static final Field HAND_TYPE_FIELD;
    private static final Method GET_ACTION_ENUM;

    static {
        try {
            ENTITY_ID = PacketPlayInUseEntity.class.getDeclaredField("a");
            ENTITY_ID.setAccessible(true);

            ACTION_TYPE = PacketPlayInUseEntity.class.getDeclaredField("b");
            ACTION_TYPE.setAccessible(true);

            final Class<?> ACTION_INTERFACE = ACTION_TYPE.getType();
            GET_ACTION_ENUM = ACTION_INTERFACE.getDeclaredMethod("a");
            GET_ACTION_ENUM.setAccessible(true);

            HAND_TYPE_FIELD = findInteractionAtLocationClass().getDeclaredField("a");
            HAND_TYPE_FIELD.setAccessible(true);

        }
        catch (final NoSuchFieldException | NoSuchMethodException e) {
            throw new RuntimeException("Failed to initialise V1_17R1FunnyGuildsChannelHandler", e);
        }
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        if (msg instanceof PacketPlayInUseEntity) {
            final PacketPlayInUseEntity packetPlayInUseEntity = (PacketPlayInUseEntity) msg;

            final int entityId = (int) ENTITY_ID.get(packetPlayInUseEntity);

            final Object actionType = ACTION_TYPE.get(packetPlayInUseEntity);
            final Enum<?> actionTypeEnum = (Enum<?>) GET_ACTION_ENUM.invoke(actionType);

            if (actionTypeEnum.ordinal() == 1) {
                //attack
                this.packetCallbacksRegistry.handleAttackEntity(entityId, true);
            }
            else if (actionTypeEnum.ordinal() == 2) {
                // interact_at
                final Enum<?> handTypeEnum = (Enum<?>) HAND_TYPE_FIELD.get(actionType);

                final boolean isMainHand = handTypeEnum.ordinal() == 0;
                this.packetCallbacksRegistry.handleRightClickEntity(entityId, isMainHand);
            }
        }

        super.channelRead(ctx, msg);
    }

    @Override
    public PacketCallbacksRegistry getPacketCallbacksRegistry() {
        return this.packetCallbacksRegistry;
    }

    private static Class<?> findInteractionAtLocationClass() {
        for (Class<?> declaredClass : PacketPlayInUseEntity.class.getDeclaredClasses()) {
            if (declaredClass.getDeclaredFields().length == 2) {
                return declaredClass;
            }
        }

        throw new IllegalStateException("Can't find InteractionAtLocationAction class");
    }
}

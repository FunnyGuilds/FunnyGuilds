package net.dzikoysk.funnyguilds.nms.v1_20R1.packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsInboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketCallbacksRegistry;
import net.minecraft.network.protocol.game.PacketPlayInUseEntity;
import org.jetbrains.annotations.NotNull;

public class V1_20R1FunnyGuildsInboundChannelHandler extends ChannelInboundHandlerAdapter implements FunnyGuildsInboundChannelHandler {

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

            Class<?> ACTION_INTERFACE = ACTION_TYPE.getType();
            GET_ACTION_ENUM = ACTION_INTERFACE.getDeclaredMethod("a");
            GET_ACTION_ENUM.setAccessible(true);

            HAND_TYPE_FIELD = findInteractionAtLocationClass().getDeclaredField("a");
            HAND_TYPE_FIELD.setAccessible(true);
        }
        catch (NoSuchFieldException | NoSuchMethodException e) {
            throw new RuntimeException("Failed to initialise V1_20R1FunnyGuildsInboundChannelHandler", e);
        }
    }

    private final PacketCallbacksRegistry packetCallbacksRegistry = new PacketCallbacksRegistry();

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        if (msg instanceof PacketPlayInUseEntity) {
            PacketPlayInUseEntity packetPlayInUseEntity = (PacketPlayInUseEntity) msg;
            int entityId = (int) ENTITY_ID.get(packetPlayInUseEntity);

            Object actionType = ACTION_TYPE.get(packetPlayInUseEntity);
            Enum<?> actionTypeEnum = (Enum<?>) GET_ACTION_ENUM.invoke(actionType);

            if (actionTypeEnum.ordinal() == 1) {
                //attack
                this.packetCallbacksRegistry.handleAttackEntity(entityId, true);
            }
            else if (actionTypeEnum.ordinal() == 2) {
                // interact_at
                Enum<?> handTypeEnum = (Enum<?>) HAND_TYPE_FIELD.get(actionType);

                boolean isMainHand = handTypeEnum.ordinal() == 0;
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

package net.dzikoysk.funnyguilds.nms.v1_13R2.packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.lang.reflect.Field;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketCallbacksRegistry;
import net.minecraft.server.v1_13_R2.EnumHand;
import net.minecraft.server.v1_13_R2.PacketPlayInUseEntity;

public class V1_13R2FunnyGuildsChannelHandler extends ChannelInboundHandlerAdapter implements FunnyGuildsChannelHandler {
    private final PacketCallbacksRegistry packetCallbacksRegistry = new PacketCallbacksRegistry();

    private static final Field ENTITY_ID;

    static {
        try {
            ENTITY_ID = PacketPlayInUseEntity.class.getDeclaredField("a");
            ENTITY_ID.setAccessible(true);

        }
        catch (final NoSuchFieldException e) {
            throw new RuntimeException("Failed to initialise V1_13R2FunnyGuildsChannelHandler", e);
        }
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        if (msg instanceof PacketPlayInUseEntity) {
            final PacketPlayInUseEntity packetPlayInUseEntity = (PacketPlayInUseEntity) msg;

            final int entityId = (int) ENTITY_ID.get(packetPlayInUseEntity);

            final PacketPlayInUseEntity.EnumEntityUseAction action = packetPlayInUseEntity.b();
            if (action == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
                this.packetCallbacksRegistry.handleAttackEntity(entityId, true);
            }
            else if (action == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT_AT) {
                final boolean isMainHand = packetPlayInUseEntity.c() == EnumHand.MAIN_HAND;
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

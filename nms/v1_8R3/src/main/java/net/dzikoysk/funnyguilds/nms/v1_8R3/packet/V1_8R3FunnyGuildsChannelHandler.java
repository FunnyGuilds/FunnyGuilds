package net.dzikoysk.funnyguilds.nms.v1_8R3.packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.lang.reflect.Field;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsInboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketCallbacksRegistry;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;

public class V1_8R3FunnyGuildsChannelHandler extends ChannelInboundHandlerAdapter implements FunnyGuildsInboundChannelHandler {
    private final PacketCallbacksRegistry packetCallbacksRegistry = new PacketCallbacksRegistry();

    private static final Field ENTITY_ID;

    static {
        try {
            ENTITY_ID = PacketPlayInUseEntity.class.getDeclaredField("a");
            ENTITY_ID.setAccessible(true);

        }
        catch (final NoSuchFieldException e) {
            throw new RuntimeException("Failed to initialise V1_8R3FunnyGuildsChannelHandler", e);
        }
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        if (msg instanceof PacketPlayInUseEntity) {
            final PacketPlayInUseEntity packetPlayInUseEntity = (PacketPlayInUseEntity) msg;

            final int entityId = (int) ENTITY_ID.get(packetPlayInUseEntity);

            final PacketPlayInUseEntity.EnumEntityUseAction action = packetPlayInUseEntity.a();
            if (action == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
                this.packetCallbacksRegistry.handleAttackEntity(entityId, true);
            }
            else if (action == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT_AT) {
                this.packetCallbacksRegistry.handleRightClickEntity(entityId, true);
            }
        }

        super.channelRead(ctx, msg);
    }

    @Override
    public PacketCallbacksRegistry getPacketCallbacksRegistry() {
        return this.packetCallbacksRegistry;
    }
}

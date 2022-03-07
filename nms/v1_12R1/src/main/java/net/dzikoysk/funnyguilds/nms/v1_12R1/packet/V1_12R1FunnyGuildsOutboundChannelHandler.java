package net.dzikoysk.funnyguilds.nms.v1_12R1.packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import java.lang.reflect.Field;
import net.dzikoysk.funnyguilds.nms.api.entity.FakeEntity;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsOutboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketSuppliersRegistry;
import net.minecraft.server.v1_12_R1.PacketPlayOutMapChunk;

public class V1_12R1FunnyGuildsOutboundChannelHandler extends ChannelOutboundHandlerAdapter implements FunnyGuildsOutboundChannelHandler {
    private final PacketSuppliersRegistry packetSuppliersRegistry = new PacketSuppliersRegistry();

    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception {
        if (msg instanceof PacketPlayOutMapChunk) {
            PacketPlayOutMapChunk chunkPacket = (PacketPlayOutMapChunk) msg;

            for (FakeEntity fakeEntity : packetSuppliersRegistry.supplyFakeEntities(chunkCoordinates(chunkPacket))) {
                ctx.write(fakeEntity.getSpawnPacket());
            }
        }
        super.write(ctx, msg, promise);
    }

    private int[] chunkCoordinates(PacketPlayOutMapChunk chunkPacket) throws NoSuchFieldException, IllegalAccessException {
        Field chunkXField = chunkPacket.getClass().getDeclaredField("a");
        Field chunkZField = chunkPacket.getClass().getDeclaredField("b");
        chunkXField.setAccessible(true);
        chunkZField.setAccessible(true);
        int xChunk = (int) chunkXField.get(chunkPacket);
        int zChunk = (int) chunkZField.get(chunkPacket);

        return new int[] {xChunk, zChunk};
    }

    @Override
    public PacketSuppliersRegistry getPacketSuppliersRegistry() {
        return packetSuppliersRegistry;
    }

}

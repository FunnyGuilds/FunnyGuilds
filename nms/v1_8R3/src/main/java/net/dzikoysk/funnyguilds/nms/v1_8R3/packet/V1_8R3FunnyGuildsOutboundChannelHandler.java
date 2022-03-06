package net.dzikoysk.funnyguilds.nms.v1_8R3.packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import net.dzikoysk.funnyguilds.nms.api.entity.FakeEntity;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsOutboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketSuppliersRegistry;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunkBulk;

public class V1_8R3FunnyGuildsOutboundChannelHandler extends ChannelOutboundHandlerAdapter implements FunnyGuildsOutboundChannelHandler {

    private final PacketSuppliersRegistry packetSuppliersRegistry = new PacketSuppliersRegistry();

    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception {
        if (msg instanceof PacketPlayOutMapChunk) {
            writeFakeEntities(ctx, chunkCoordinates((PacketPlayOutMapChunk) msg));
        } else if (msg instanceof PacketPlayOutMapChunkBulk) {
            for (int[] chunkCoordinates : chunksCoordinates((PacketPlayOutMapChunkBulk) msg)) {
                writeFakeEntities(ctx, chunkCoordinates);
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

    private Collection<int[]> chunksCoordinates(PacketPlayOutMapChunkBulk chunkBulkPacket) throws NoSuchFieldException, IllegalAccessException {
        Field chunksXField = chunkBulkPacket.getClass().getDeclaredField("a");
        Field chunksZField = chunkBulkPacket.getClass().getDeclaredField("b");
        chunksXField.setAccessible(true);
        chunksZField.setAccessible(true);
        int[]xChunks = (int[]) chunksXField.get(chunkBulkPacket);
        int[]zChunks = (int[]) chunksZField.get(chunkBulkPacket);

        HashSet<int[]> chunksCoordinates = new HashSet<>();
        for (int i = 0; i < xChunks.length; i++) {
            chunksCoordinates.add(new int[] {xChunks[i], zChunks[i]});
        }
        return chunksCoordinates;
    }

    private void writeFakeEntities(ChannelHandlerContext ctx, int[] chunkCoordinates) {
        for (FakeEntity fakeEntity : packetSuppliersRegistry.supplyFakeEntities()) {
            Object spawnPacket = fakeEntity.getSpawnPacket();

            int[] spawnChunkCoordinates = fakeEntity.getChunkCoordinates();

            if(spawnChunkCoordinates[0] != chunkCoordinates[0]
                    || spawnChunkCoordinates[1] != chunkCoordinates[1]) {
                continue;
            }

            ctx.write(spawnPacket);
        }
    }

    @Override
    public PacketSuppliersRegistry getPacketSuppliersRegistry() {
        return packetSuppliersRegistry;
    }

}

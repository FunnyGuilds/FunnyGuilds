package net.dzikoysk.funnyguilds.nms.v1_12R1.packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import java.lang.reflect.Field;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsOutboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketSuppliersRegistry;
import net.dzikoysk.funnyguilds.nms.v1_8R3.entity.ObjectType;
import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityLiving;
import net.minecraft.server.v1_12_R1.Packet;
import net.minecraft.server.v1_12_R1.PacketPlayOutMapChunk;
import net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.EntityType;
import panda.std.Pair;

public class V1_12R1FunnyGuildsOutboundChannelHandler extends ChannelOutboundHandlerAdapter implements FunnyGuildsOutboundChannelHandler {
    private final PacketSuppliersRegistry packetSuppliersRegistry = new PacketSuppliersRegistry();

    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception {
        if (msg instanceof PacketPlayOutMapChunk) {
            PacketPlayOutMapChunk packet = (PacketPlayOutMapChunk) msg;
            for (Pair<Location, EntityType> fakeEntity : packetSuppliersRegistry.supplyFakeEntities()) {
                Location location = fakeEntity.getFirst();
                Chunk chunk = location.getChunk();
                EntityType entityType = fakeEntity.getSecond();

                Field xField = packet.getClass().getDeclaredField("a");
                Field zField = packet.getClass().getDeclaredField("b");

                xField.setAccessible(true);
                zField.setAccessible(true);

                int x = (int) xField.get(packet);
                int z = (int) zField.get(packet);

                if(chunk.getX() != x || chunk.getZ() != z) {
                    continue;
                }

                CraftWorld world = ((CraftWorld) location.getWorld());

                if (world == null) {
                    throw new IllegalStateException("location's world is null!");
                }

                Entity entity = world.createEntity(location, entityType.getEntityClass());
                Packet<?> spawnEntityPacket;

                if (entity instanceof EntityLiving) {
                    ctx.write(new PacketPlayOutSpawnEntityLiving((EntityLiving) entity));
                }
                else {
                    ctx.write(new PacketPlayOutSpawnEntity(entity, ObjectType.getIdFor(entityType)));
                }
            }
        }
        super.write(ctx, msg, promise);
    }

    @Override
    public PacketSuppliersRegistry getPacketSuppliersRegistry() {
        return packetSuppliersRegistry;
    }
}

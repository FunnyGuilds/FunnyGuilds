package net.dzikoysk.funnyguilds.nms.v1_8R3.entity;

import com.google.common.base.Preconditions;
import net.dzikoysk.funnyguilds.nms.api.entity.EntityAccessor;
import net.dzikoysk.funnyguilds.nms.api.entity.FakeEntity;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class V1_8R3EntityAccessor implements EntityAccessor {

    @Override
    public FakeEntity createFakeEntity(EntityType entityType, Location location) {
        Preconditions.checkNotNull(entityType, "entity type can't be null!");
        Preconditions.checkNotNull(location, "location can't be null!");
        Preconditions.checkArgument(entityType.isSpawnable(), "entity type is not spawnable!");

        CraftWorld world = ((CraftWorld) location.getWorld());

        if (world == null) {
            throw new IllegalStateException("location's world is null!");
        }

        Entity entity = world.createEntity(location, entityType.getEntityClass());
        Packet<?> spawnEntityPacket;

        if (entity instanceof EntityLiving) {
            spawnEntityPacket = new PacketPlayOutSpawnEntityLiving((EntityLiving) entity);
        }
        else {
            spawnEntityPacket = new PacketPlayOutSpawnEntity(entity, entityType.getTypeId());
        }

        return new FakeEntity(entity.getId(), spawnEntityPacket);
    }

    @Override
    public void spawnFakeEntityFor(FakeEntity entity, Player... players) {
        for (Player player : players) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket((Packet<?>) entity.getSpawnPacket());
        }
    }

    @Override
    public void despawnFakeEntityFor(FakeEntity entity, Player... players) {
        PacketPlayOutEntityDestroy destroyEntityPacket = new PacketPlayOutEntityDestroy(entity.getId());

        for (Player player : players) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(destroyEntityPacket);
        }
    }
}

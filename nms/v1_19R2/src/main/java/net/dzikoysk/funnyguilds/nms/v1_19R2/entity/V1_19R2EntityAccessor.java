package net.dzikoysk.funnyguilds.nms.v1_19R2.entity;

import com.google.common.base.Preconditions;
import net.dzikoysk.funnyguilds.nms.api.entity.EntityAccessor;
import net.dzikoysk.funnyguilds.nms.api.entity.FakeEntity;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class V1_19R2EntityAccessor implements EntityAccessor {

    @Override
    public FakeEntity createFakeEntity(EntityType entityType, Location location) {
        Preconditions.checkNotNull(entityType, "entity type can't be null!");
        Preconditions.checkNotNull(location, "location can't be null!");
        Preconditions.checkArgument(entityType.isSpawnable(), "entity type is not spawnable!");

        CraftWorld world = ((CraftWorld) location.getWorld());
        if (world == null) {
            throw new IllegalStateException("location's world is null!");
        }

        net.minecraft.world.entity.Entity entity = world.createEntity(location, entityType.getEntityClass());
        Packet<?> spawnEntityPacket;

        spawnEntityPacket = new PacketPlayOutSpawnEntity(entity);

        return new FakeEntity(entity.ah(), location, spawnEntityPacket); // ah() zwraca aT czyli chyba getId
    }

    @Override
    public void spawnFakeEntityFor(FakeEntity entity, Player... players) {
        for (Player player : players) {
            ((CraftPlayer) player).getHandle().b.a((Packet<?>) entity.getSpawnPacket()); // sendPacket -> a
        }
    }

    @Override
    public void despawnFakeEntityFor(FakeEntity entity, Player... players) {
        PacketPlayOutEntityDestroy destroyEntityPacket = new PacketPlayOutEntityDestroy(entity.getId());

        for (Player player : players) {
            ((CraftPlayer) player).getHandle().b.a(destroyEntityPacket); // sendPacket -> a
        }
    }

}

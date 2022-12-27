package net.dzikoysk.funnyguilds.nms.api.packet;

import java.util.Collection;
import net.dzikoysk.funnyguilds.nms.api.entity.FakeEntity;
import org.bukkit.World;

public interface PacketSuppliers {

    Collection<FakeEntity> supplyFakeEntities(World world, int chunkX, int chunkZ);

}

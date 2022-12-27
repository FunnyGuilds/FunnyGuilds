package net.dzikoysk.funnyguilds.nms.api.packet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.dzikoysk.funnyguilds.nms.api.entity.FakeEntity;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class PacketSuppliersRegistry implements PacketSuppliers {

    private Player owner;
    private final List<PacketSuppliers> packetSuppliers = new ArrayList<>();

    public Player getOwner() {
        return this.owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public void registerPacketSupplier(PacketSuppliers suppliers) {
        this.packetSuppliers.add(suppliers);
    }

    @Override
    public Set<FakeEntity> supplyFakeEntities(World world, int chunkX, int chunkZ) {
        Set<FakeEntity> suppliedEntities = new HashSet<>();
        for (PacketSuppliers packetSupplier : this.packetSuppliers) {
            suppliedEntities.addAll(packetSupplier.supplyFakeEntities(world, chunkX, chunkZ));
        }
        return suppliedEntities;
    }

    public Set<FakeEntity> supplyFakeEntities(int chunkX, int chunkZ) {
        return this.supplyFakeEntities(this.owner.getWorld(), chunkX, chunkZ);
    }

}

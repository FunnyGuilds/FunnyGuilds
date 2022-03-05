package net.dzikoysk.funnyguilds.nms.api.packet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.dzikoysk.funnyguilds.nms.api.entity.FakeEntity;

public class PacketSuppliersRegistry implements PacketSuppliers {

    private final List<PacketSuppliers> packetSuppliers = new ArrayList<>();

    public void registerPacketSupplier(PacketSuppliers suppliers) {
        this.packetSuppliers.add(suppliers);
    }

    @Override
    public Set<FakeEntity> supplyFakeEntities() {
        Set<FakeEntity> suppliedEntities = new HashSet<>();
        for (PacketSuppliers packetSupplier : this.packetSuppliers) {
            suppliedEntities.addAll(packetSupplier.supplyFakeEntities());
        }
        return suppliedEntities;
    }
}

package net.dzikoysk.funnyguilds.nms.api.packet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import panda.std.Pair;

public class PacketSuppliersRegistry implements PacketSuppliers {

    private final List<PacketSuppliers> packetSuppliers = new ArrayList<>();

    public void registerPacketSupplier(PacketSuppliers suppliers) {
        this.packetSuppliers.add(suppliers);
    }

    @Override
    public Set<Pair<Location, EntityType>> supplyFakeEntities() {
        Set<Pair<Location, EntityType>> suppliedEntities = new HashSet<>();
        for (PacketSuppliers packetSupplier : this.packetSuppliers) {
            suppliedEntities.addAll(packetSupplier.supplyFakeEntities());
        }
        return suppliedEntities;
    }
}

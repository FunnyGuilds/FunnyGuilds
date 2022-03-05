package net.dzikoysk.funnyguilds.nms.api.packet;

import java.util.Set;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import panda.std.Pair;

public interface PacketSuppliers {

    Set<Pair<Location, EntityType>> supplyFakeEntities();

}

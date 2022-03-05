package net.dzikoysk.funnyguilds.nms.api.packet;

import java.util.Collection;
import net.dzikoysk.funnyguilds.nms.api.entity.FakeEntity;

public interface PacketSuppliers {

    Collection<FakeEntity> supplyFakeEntities();

}

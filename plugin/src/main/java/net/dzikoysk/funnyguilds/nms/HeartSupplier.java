package net.dzikoysk.funnyguilds.nms;

import java.util.Collection;
import net.dzikoysk.funnyguilds.nms.api.entity.FakeEntity;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketSuppliers;

public class HeartSupplier implements PacketSuppliers {

    @Override
    public Collection<FakeEntity> supplyFakeEntities() {
        return GuildEntityHelper.getGuildEntities().values();
    }

}

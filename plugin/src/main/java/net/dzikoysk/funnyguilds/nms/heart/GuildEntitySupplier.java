package net.dzikoysk.funnyguilds.nms.heart;

import java.util.Collection;
import net.dzikoysk.funnyguilds.nms.api.entity.FakeEntity;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketSuppliers;

public class GuildEntitySupplier implements PacketSuppliers {

    private final GuildEntityHelper helper;

    public GuildEntitySupplier(GuildEntityHelper helper) {
        this.helper = helper;
    }

    @Override
    public Collection<FakeEntity> supplyFakeEntities() {
        return helper.getGuildEntities().values();
    }

}

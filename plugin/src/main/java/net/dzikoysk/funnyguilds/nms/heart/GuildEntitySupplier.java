package net.dzikoysk.funnyguilds.nms.heart;

import java.util.Collection;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.nms.api.entity.FakeEntity;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketSuppliers;

public class GuildEntitySupplier implements PacketSuppliers {

    private final GuildEntityHelper helper;

    public GuildEntitySupplier(GuildEntityHelper helper) {
        this.helper = helper;
    }

    @Override
    public Collection<FakeEntity> supplyFakeEntities(int[] chunkCoordinates) {
        return this.helper.getGuildEntities().values()
                .stream()
                .filter(entity -> {
                    int[] entityChunkCoordinates = entity.getChunkCoordinates();
                    return entityChunkCoordinates[0] == chunkCoordinates[0] && entityChunkCoordinates[1] == chunkCoordinates[1];
                })
                .collect(Collectors.toSet());
    }

}

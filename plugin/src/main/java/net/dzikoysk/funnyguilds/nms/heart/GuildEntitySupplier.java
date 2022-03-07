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
    public Collection<FakeEntity> supplyFakeEntities(int xChunk, int zChunk) {
        return this.helper.getGuildEntities().values()
                .stream()
                .filter(entity -> {
                    int[] entityChunkCoordinates = entity.getChunkCoordinates();
                    return entityChunkCoordinates[0] == xChunk && entityChunkCoordinates[1] == zChunk;
                })
                .collect(Collectors.toSet());
    }

}

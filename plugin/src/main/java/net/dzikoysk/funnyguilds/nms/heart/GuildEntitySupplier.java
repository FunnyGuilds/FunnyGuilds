package net.dzikoysk.funnyguilds.nms.heart;

import java.util.Collection;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.nms.api.entity.FakeEntity;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketSuppliers;
import org.bukkit.World;

public class GuildEntitySupplier implements PacketSuppliers {

    private final GuildEntityHelper helper;

    public GuildEntitySupplier(GuildEntityHelper helper) {
        this.helper = helper;
    }

    @Override
    public Collection<FakeEntity> supplyFakeEntities(World world, int chunkX, int chunkZ) {
        return this.helper.getGuildEntities().values().stream()
                .filter(entity -> entity.getLocation().getWorld().equals(world))
                .filter(entity -> entity.getChunkX() == chunkX)
                .filter(entity -> entity.getChunkZ() == chunkZ)
                .collect(Collectors.toUnmodifiableSet());
    }

}

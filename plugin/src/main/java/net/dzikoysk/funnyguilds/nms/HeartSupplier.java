package net.dzikoysk.funnyguilds.nms;

import java.util.Set;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketSuppliers;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import panda.std.Pair;
import panda.std.stream.PandaStream;

public class HeartSupplier implements PacketSuppliers {

    @Override
    public Set<Pair<Location, EntityType>> supplyFakeEntities() {
        return PandaStream.of(FunnyGuilds.getInstance().getGuildManager().getGuilds())
                .flatMap(Guild::getEnderCrystal)
                .map(location -> Pair.of(location, EntityType.ENDER_CRYSTAL))
                .collect(Collectors.toSet());
    }

}

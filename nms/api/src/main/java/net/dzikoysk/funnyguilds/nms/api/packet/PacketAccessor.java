package net.dzikoysk.funnyguilds.nms.api.packet;

import org.bukkit.entity.Player;

public interface PacketAccessor {
    FunnyGuildsInboundChannelHandler getOrInstallInboundChannelHandler(Player player);
    default FunnyGuildsOutboundChannelHandler getOrInstallOutboundChannelHandler(Player player) {
        return null;
    }
}

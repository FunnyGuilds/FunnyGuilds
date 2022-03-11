package net.dzikoysk.funnyguilds.nms.api.packet;

import org.bukkit.entity.Player;

public interface PacketAccessor {
    FunnyGuildsInboundChannelHandler getOrInstallInboundChannelHandler(Player player);

    FunnyGuildsOutboundChannelHandler getOrInstallOutboundChannelHandler(Player player);
}

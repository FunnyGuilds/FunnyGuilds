package net.dzikoysk.funnyguilds.nms.api.packet;

import org.bukkit.entity.Player;

public interface PacketAccessor {
    FunnyGuildsChannelHandler getOrInstallChannelHandler(Player player);
}

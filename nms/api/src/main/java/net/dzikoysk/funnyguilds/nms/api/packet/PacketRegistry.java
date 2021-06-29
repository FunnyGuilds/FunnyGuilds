package net.dzikoysk.funnyguilds.nms.api.packet;

import org.bukkit.entity.Player;

public interface PacketRegistry {
    void registerPacketCallbacks(Player player, PacketCallbacks callbacks);
}

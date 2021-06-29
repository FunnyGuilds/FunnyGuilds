package net.dzikoysk.funnyguilds.nms.api.packet;

public interface PacketRegistry {
    void registerPacketCallbacks(Player player, PacketCallbacks callbacks);
}

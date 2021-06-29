package net.dzikoysk.funnyguilds.nms.api.packet;

public interface PacketCallbacks {
    void handleRightClickEntity(int entityId);

    void handleAttackEntity(int entityId);
}

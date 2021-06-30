package net.dzikoysk.funnyguilds.nms.api.packet;

public interface PacketCallbacks {
    void handleRightClickEntity(int entityId, boolean isMainHand);

    void handleAttackEntity(int entityId, boolean isMainHand);
}

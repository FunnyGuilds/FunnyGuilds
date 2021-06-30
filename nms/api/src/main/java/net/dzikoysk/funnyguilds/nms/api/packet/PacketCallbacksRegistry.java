package net.dzikoysk.funnyguilds.nms.api.packet;

import java.util.ArrayList;
import java.util.List;

public class PacketCallbacksRegistry implements PacketCallbacks {

    private final List<PacketCallbacks> packetCallbacks = new ArrayList<>();

    public void registerPacketCallback(PacketCallbacks callbacks) {
        this.packetCallbacks.add(callbacks);
    }

    @Override
    public void handleRightClickEntity(int entityId, boolean isMainHand) {
        for (PacketCallbacks packetCallback : this.packetCallbacks) {
            packetCallback.handleRightClickEntity(entityId, isMainHand);
        }
    }

    @Override
    public void handleAttackEntity(int entityId, boolean isMainHand) {
        for (PacketCallbacks packetCallback : this.packetCallbacks) {
            packetCallback.handleAttackEntity(entityId, isMainHand);
        }
    }
}

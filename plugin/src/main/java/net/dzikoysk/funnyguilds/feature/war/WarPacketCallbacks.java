package net.dzikoysk.funnyguilds.feature.war;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.requests.war.WarAttackRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.war.WarInfoRequest;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketCallbacks;
import net.dzikoysk.funnyguilds.user.User;

public class WarPacketCallbacks implements PacketCallbacks {
    private final User user;

    public WarPacketCallbacks(final User user) {
        this.user = user;
    }

    @Override
    public void handleRightClickEntity(int entityId, boolean isMainHand) {
        FunnyGuilds.getInstance().getConcurrencyManager().postRequests(new WarInfoRequest(FunnyGuilds.getInstance(), this.user, entityId));
    }

    @Override
    public void handleAttackEntity(int entityId, boolean isMainHand) {
        if (!isMainHand) {
            return;
        }

        FunnyGuilds.getInstance().getConcurrencyManager().postRequests(new WarAttackRequest(this.user, entityId));
    }
}

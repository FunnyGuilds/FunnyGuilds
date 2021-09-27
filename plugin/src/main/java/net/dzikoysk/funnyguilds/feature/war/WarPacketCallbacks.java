package net.dzikoysk.funnyguilds.feature.war;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.requests.war.WarAttackRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.war.WarInfoRequest;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketCallbacks;
import org.bukkit.entity.Player;

public class WarPacketCallbacks implements PacketCallbacks {
    private final Player player;

    public WarPacketCallbacks(final Player player) {
        this.player = player;
    }

    @Override
    public void handleRightClickEntity(int entityId, boolean isMainHand) {
        FunnyGuilds.getInstance().getConcurrencyManager().postRequests(new WarInfoRequest(FunnyGuilds.getInstance(), this.player, entityId));
    }

    @Override
    public void handleAttackEntity(int entityId, boolean isMainHand) {
        if (!isMainHand) {
            return;
        }

        FunnyGuilds.getInstance().getConcurrencyManager().postRequests(new WarAttackRequest(this.player, entityId));
    }
}

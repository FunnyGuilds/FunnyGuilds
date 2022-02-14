package net.dzikoysk.funnyguilds.feature.war;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.requests.war.WarAttackRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.war.WarInfoRequest;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketCallbacks;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.entity.Player;

public class WarPacketCallbacks implements PacketCallbacks {
    private final Player player;
    private final UserManager userManager;

    public WarPacketCallbacks(final Player player, final UserManager userManager) {
        this.player = player;
        this.userManager = userManager;
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

        FunnyGuilds.getInstance().getConcurrencyManager().postRequests(new WarAttackRequest(this.player, entityId, this.userManager));
    }
}

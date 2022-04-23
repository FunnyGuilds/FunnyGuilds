package net.dzikoysk.funnyguilds.feature.war;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.requests.war.WarAttackRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.war.WarInfoRequest;
import net.dzikoysk.funnyguilds.nms.api.packet.PacketCallbacks;
import net.dzikoysk.funnyguilds.user.User;

public class WarPacketCallbacks implements PacketCallbacks {

    private final FunnyGuilds plugin;
    private final User user;

    public WarPacketCallbacks(FunnyGuilds plugin, User user) {
        this.plugin = plugin;
        this.user = user;
    }

    @Override
    public void handleRightClickEntity(int entityId, boolean isMainHand) {
        plugin.getConcurrencyManager().postRequests(new WarInfoRequest(plugin, this.plugin.getGuildEntityHelper(), user, entityId));
    }

    @Override
    public void handleAttackEntity(int entityId, boolean isMainHand) {
        if (!isMainHand) {
            return;
        }

        plugin.getConcurrencyManager().postRequests(new WarAttackRequest(plugin, plugin.getGuildEntityHelper(), user, entityId));
    }
}

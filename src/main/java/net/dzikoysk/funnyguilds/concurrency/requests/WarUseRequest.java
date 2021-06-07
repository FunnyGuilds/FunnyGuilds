package net.dzikoysk.funnyguilds.concurrency.requests;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.system.war.WarSystem;
import org.bukkit.entity.Player;

public class WarUseRequest extends DefaultConcurrencyRequest {

    private final Player player;
    private final Object packet;
    private final WarSystem warSystem;

    public WarUseRequest(Player player, Object packet, FunnyGuilds plugin) {
        this.player = player;
        this.packet = packet;
        this.warSystem = plugin.getSystemManager().getWarSystem();
    }

    @Override
    public void execute() {
        warSystem.getWarListener().use(player, packet);
    }

}

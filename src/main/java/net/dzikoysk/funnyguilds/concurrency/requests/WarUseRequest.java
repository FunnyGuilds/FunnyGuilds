package net.dzikoysk.funnyguilds.concurrency.requests;

import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.system.war.WarListener;
import org.bukkit.entity.Player;

public class WarUseRequest extends DefaultConcurrencyRequest {

    private final Player player;
    private final Object packet;

    public WarUseRequest(Player player, Object packet) {
        this.player = player;
        this.packet = packet;
    }

    @Override
    public void execute() throws Exception {
        WarListener.use(player, packet);
    }

}

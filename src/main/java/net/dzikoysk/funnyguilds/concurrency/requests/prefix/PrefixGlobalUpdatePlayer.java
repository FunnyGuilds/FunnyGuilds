package net.dzikoysk.funnyguilds.concurrency.requests.prefix;

import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.element.IndividualPrefixManager;
import org.bukkit.entity.Player;

public class PrefixGlobalUpdatePlayer extends DefaultConcurrencyRequest {

    private final Player player;

    public PrefixGlobalUpdatePlayer(Player player) {
        this.player = player;
    }

    @Override
    public void execute() throws Exception {
        IndividualPrefixManager.updatePlayer(player);
    }

}

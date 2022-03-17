package net.dzikoysk.funnyguilds.concurrency.requests.prefix;

import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.feature.prefix.IndividualPrefixManager;
import org.bukkit.entity.Player;

public class PrefixGlobalUpdatePlayer extends DefaultConcurrencyRequest {

    private final IndividualPrefixManager individualPrefixManager;

    private final Player player;

    public PrefixGlobalUpdatePlayer(IndividualPrefixManager individualPrefixManager, Player player) {
        this.individualPrefixManager = individualPrefixManager;
        this.player = player;
    }

    @Override
    public void execute() throws Exception {
        this.individualPrefixManager.updatePlayer(player);
    }

}

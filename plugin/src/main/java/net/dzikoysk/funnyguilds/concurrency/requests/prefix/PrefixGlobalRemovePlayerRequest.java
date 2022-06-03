package net.dzikoysk.funnyguilds.concurrency.requests.prefix;

import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.feature.prefix.IndividualPrefixManager;

public class PrefixGlobalRemovePlayerRequest extends DefaultConcurrencyRequest {

    private final IndividualPrefixManager individualPrefixManager;
    private final String player;

    public PrefixGlobalRemovePlayerRequest(IndividualPrefixManager individualPrefixManager, String player) {
        this.individualPrefixManager = individualPrefixManager;
        this.player = player;
    }

    @Override
    public void execute() throws Exception {
        this.individualPrefixManager.removePlayer(this.player);
    }

}

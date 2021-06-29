package net.dzikoysk.funnyguilds.concurrency.requests.prefix;

import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.element.IndividualPrefixManager;

public class PrefixGlobalAddPlayerRequest extends DefaultConcurrencyRequest {

    private final String player;

    public PrefixGlobalAddPlayerRequest(String player) {
        this.player = player;
    }

    @Override
    public void execute() throws Exception {
        IndividualPrefixManager.addPlayer(player);
    }

}

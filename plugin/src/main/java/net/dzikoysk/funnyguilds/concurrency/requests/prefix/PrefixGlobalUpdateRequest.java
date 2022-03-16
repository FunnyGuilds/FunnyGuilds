package net.dzikoysk.funnyguilds.concurrency.requests.prefix;

import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.feature.prefix.IndividualPrefixManager;

public class PrefixGlobalUpdateRequest extends DefaultConcurrencyRequest {

    private final IndividualPrefixManager individualPrefixManager;

    public PrefixGlobalUpdateRequest(IndividualPrefixManager individualPrefixManager) {
        this.individualPrefixManager = individualPrefixManager;
    }

    @Override
    public void execute() throws Exception {
        this.individualPrefixManager.updatePlayers();
    }

}

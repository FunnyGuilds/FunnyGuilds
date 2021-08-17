package net.dzikoysk.funnyguilds.concurrency.requests.prefix;

import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.feature.prefix.IndividualPrefixManager;

public class PrefixGlobalUpdateRequest extends DefaultConcurrencyRequest {

    @Override
    public void execute() throws Exception {
        IndividualPrefixManager.updatePlayers();
    }

}

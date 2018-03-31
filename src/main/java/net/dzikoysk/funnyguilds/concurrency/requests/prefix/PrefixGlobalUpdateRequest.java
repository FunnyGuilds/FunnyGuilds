package net.dzikoysk.funnyguilds.concurrency.requests.prefix;

import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.element.IndividualPrefixManager;

public class PrefixGlobalUpdateRequest extends DefaultConcurrencyRequest {

    @Override
    public void execute() throws Exception {
        IndividualPrefixManager.updatePlayers();
    }

}

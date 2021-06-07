package net.dzikoysk.funnyguilds.concurrency.requests.prefix;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.element.IndividualPrefixManager;

public class PrefixGlobalUpdateRequest extends DefaultConcurrencyRequest {

    private final IndividualPrefixManager individualPrefixManager;

    public PrefixGlobalUpdateRequest(FunnyGuilds plugin) {
        this.individualPrefixManager = plugin.getSystemManager().getIndividualPrefixManager();
    }

    @Override
    public void execute() {
        individualPrefixManager.updatePlayers();
    }

}

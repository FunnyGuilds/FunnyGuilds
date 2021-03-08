package net.dzikoysk.funnyguilds.concurrency.requests.dummy;

import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.element.DummyManager;

public class DummyGlobalUpdateRequest extends DefaultConcurrencyRequest {

    @Override
    public void execute() {
        DummyManager.updatePlayers();
    }

}

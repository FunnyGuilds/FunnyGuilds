package net.dzikoysk.funnyguilds.feature.scoreboard.dummy;

import net.dzikoysk.funnyguilds.shared.FunnyTask.SyncFunnyTask;
import panda.std.Option;

public class DummyGlobalUpdateSyncTask extends SyncFunnyTask {

    private final Option<DummyManager> dummyManager;

    public DummyGlobalUpdateSyncTask(Option<DummyManager> dummyManager) {
        this.dummyManager = dummyManager;
    }

    @Override
    public void execute() {
        this.dummyManager.peek(DummyManager::updatePlayers);
    }

}

package net.dzikoysk.funnyguilds.feature.scoreboard.dummy;

import net.dzikoysk.funnyguilds.shared.FunnyTask.SyncFunnyTask;

public class DummyGlobalUpdateSyncTask extends SyncFunnyTask {

    private final DummyManager dummyManager;

    public DummyGlobalUpdateSyncTask(DummyManager dummyManager) {
        this.dummyManager = dummyManager;
    }

    @Override
    public void execute() {
        this.dummyManager.updatePlayers();
    }

}

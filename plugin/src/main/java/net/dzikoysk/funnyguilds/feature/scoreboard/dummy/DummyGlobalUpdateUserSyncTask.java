package net.dzikoysk.funnyguilds.feature.scoreboard.dummy;

import net.dzikoysk.funnyguilds.feature.scoreboard.dummy.DummyManager;
import net.dzikoysk.funnyguilds.shared.FunnyTask.SyncFunnyTask;
import net.dzikoysk.funnyguilds.user.User;

public class DummyGlobalUpdateUserSyncTask extends SyncFunnyTask {

    private final DummyManager dummyManager;
    private final User user;

    public DummyGlobalUpdateUserSyncTask(DummyManager dummyManager, User user) {
        this.dummyManager = dummyManager;
        this.user = user;
    }

    @Override
    public void execute() {
        this.dummyManager.updateScore(this.user);
    }

}

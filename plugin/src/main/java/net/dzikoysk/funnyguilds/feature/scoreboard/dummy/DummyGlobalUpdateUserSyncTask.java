package net.dzikoysk.funnyguilds.feature.scoreboard.dummy;

import net.dzikoysk.funnyguilds.shared.FunnyTask.SyncFunnyTask;
import net.dzikoysk.funnyguilds.user.User;
import panda.std.Option;

public class DummyGlobalUpdateUserSyncTask extends SyncFunnyTask {

    private final Option<DummyManager> dummyManager;
    private final User user;

    public DummyGlobalUpdateUserSyncTask(Option<DummyManager> dummyManager, User user) {
        this.dummyManager = dummyManager;
        this.user = user;
    }

    @Override
    public void execute() {
        this.dummyManager.peek(manager -> manager.updateScore(this.user));
    }

}

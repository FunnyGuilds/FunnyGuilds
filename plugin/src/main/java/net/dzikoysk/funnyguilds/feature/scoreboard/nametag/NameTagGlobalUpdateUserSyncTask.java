package net.dzikoysk.funnyguilds.feature.scoreboard.nametag;

import net.dzikoysk.funnyguilds.shared.FunnyTask.SyncFunnyTask;
import net.dzikoysk.funnyguilds.user.User;
import panda.std.Option;

public class NameTagGlobalUpdateUserSyncTask extends SyncFunnyTask {

    private final Option<IndividualNameTagManager> individualNameTagManager;
    private final User user;

    public NameTagGlobalUpdateUserSyncTask(Option<IndividualNameTagManager> individualNameTagManager, User user) {
        this.individualNameTagManager = individualNameTagManager;
        this.user = user;
    }

    @Override
    public void execute() throws Exception {
        this.individualNameTagManager.peek(manager -> manager.updatePlayer(this.user));
    }

}

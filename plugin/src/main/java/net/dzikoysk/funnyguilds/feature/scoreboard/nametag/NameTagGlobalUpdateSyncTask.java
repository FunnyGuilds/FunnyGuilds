package net.dzikoysk.funnyguilds.feature.scoreboard.nametag;

import net.dzikoysk.funnyguilds.shared.FunnyTask.SyncFunnyTask;
import panda.std.Option;

public class NameTagGlobalUpdateSyncTask extends SyncFunnyTask {

    private final Option<IndividualNameTagManager> individualNameTagManager;

    public NameTagGlobalUpdateSyncTask(Option<IndividualNameTagManager> individualNameTagManager) {
        this.individualNameTagManager = individualNameTagManager;
    }

    @Override
    public void execute() throws Exception {
        this.individualNameTagManager.peek(IndividualNameTagManager::updatePlayers);
    }

}

package net.dzikoysk.funnyguilds.feature.scoreboard.nametag;

import net.dzikoysk.funnyguilds.feature.scoreboard.nametag.IndividualNameTagManager;
import net.dzikoysk.funnyguilds.shared.FunnyTask.SyncFunnyTask;

public class NameTagGlobalUpdateSyncTask extends SyncFunnyTask {

    private final IndividualNameTagManager individualNameTagManager;

    public NameTagGlobalUpdateSyncTask(IndividualNameTagManager individualNameTagManager) {
        this.individualNameTagManager = individualNameTagManager;
    }

    @Override
    public void execute() throws Exception {
        this.individualNameTagManager.updatePlayers();
    }

}

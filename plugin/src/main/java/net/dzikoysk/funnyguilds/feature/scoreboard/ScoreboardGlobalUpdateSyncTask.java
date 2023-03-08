package net.dzikoysk.funnyguilds.feature.scoreboard;

import net.dzikoysk.funnyguilds.shared.FunnyTask.SyncFunnyTask;

public class ScoreboardGlobalUpdateSyncTask extends SyncFunnyTask {

    private final AbstractScoreboardHandler<?> scoreboardHandler;

    public ScoreboardGlobalUpdateSyncTask(AbstractScoreboardHandler<?> scoreboardHandler) {
        this.scoreboardHandler = scoreboardHandler;
    }

    @Override
    public void execute() throws Exception {
        this.scoreboardHandler.updatePlayers();
    }

}

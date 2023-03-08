package net.dzikoysk.funnyguilds.feature.scoreboard;

import net.dzikoysk.funnyguilds.shared.FunnyTask.SyncFunnyTask;

public class ScoreboardGlobalUpdateSyncTask extends SyncFunnyTask {

    private final AbstractScoreboardHandler<?> scoreboardHandler;
    private final boolean highPriority;

    public ScoreboardGlobalUpdateSyncTask(AbstractScoreboardHandler<?> scoreboardHandler, boolean highPriority) {
        this.scoreboardHandler = scoreboardHandler;
        this.highPriority = highPriority;
    }

    public ScoreboardGlobalUpdateSyncTask(AbstractScoreboardHandler<?> scoreboardHandler) {
        this(scoreboardHandler, true);
    }

    @Override
    public void execute() throws Exception {
        this.scoreboardHandler.updatePlayers(this.highPriority);
    }

}

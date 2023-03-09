package net.dzikoysk.funnyguilds.feature.scoreboard;

import net.dzikoysk.funnyguilds.shared.FunnyTask.SyncFunnyTask;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;

public class ScoreboardGlobalUpdateUserSyncTask extends SyncFunnyTask {

    private final AbstractScoreboardHandler<?> scoreboardHandler;
    private final User user;
    private final boolean highPriority;

    public ScoreboardGlobalUpdateUserSyncTask(AbstractScoreboardHandler<?> scoreboardHandler, User user, boolean highPriority) {
        this.scoreboardHandler = scoreboardHandler;
        this.user = user;
        this.highPriority = highPriority;
    }

    public ScoreboardGlobalUpdateUserSyncTask(AbstractScoreboardHandler<?> scoreboardHandler, User user) {
        this(scoreboardHandler, user, true);
    }

    @Override
    public void execute() throws Exception {
        this.scoreboardHandler.updatePlayer(Bukkit.getPlayer(this.user.getUUID()), this.user, this.highPriority);
    }

}

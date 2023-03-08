package net.dzikoysk.funnyguilds.feature.scoreboard;

import net.dzikoysk.funnyguilds.shared.FunnyTask.SyncFunnyTask;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;

public class ScoreboardGlobalUpdateUserSyncTask extends SyncFunnyTask {

    private final AbstractScoreboardHandler<?> scoreboardHandler;
    private final User user;

    public ScoreboardGlobalUpdateUserSyncTask(AbstractScoreboardHandler<?> scoreboardHandler, User user) {
        this.scoreboardHandler = scoreboardHandler;
        this.user = user;
    }

    @Override
    public void execute() throws Exception {
        this.scoreboardHandler.updatePlayer(Bukkit.getPlayer(this.user.getUUID()), this.user);
    }

}

package net.dzikoysk.funnyguilds.feature.scoreboard.nametag;

import net.dzikoysk.funnyguilds.feature.scoreboard.nametag.IndividualNameTagManager;
import net.dzikoysk.funnyguilds.shared.FunnyTask.SyncFunnyTask;
import net.dzikoysk.funnyguilds.user.User;

public class NameTagGlobalUpdateUserSyncTask extends SyncFunnyTask {

    private final IndividualNameTagManager individualNameTagManager;
    private final User user;

    public NameTagGlobalUpdateUserSyncTask(IndividualNameTagManager individualNameTagManager, User user) {
        this.individualNameTagManager = individualNameTagManager;
        this.user = user;
    }

    @Override
    public void execute() throws Exception {
        this.individualNameTagManager.updatePlayer(this.user);
    }

}

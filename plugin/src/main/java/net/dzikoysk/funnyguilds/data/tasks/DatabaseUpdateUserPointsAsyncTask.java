package net.dzikoysk.funnyguilds.data.tasks;

import net.dzikoysk.funnyguilds.data.database.serializer.DatabaseUserSerializer;
import net.dzikoysk.funnyguilds.shared.FunnyTask.AsyncFunnyTask;
import net.dzikoysk.funnyguilds.user.User;

public class DatabaseUpdateUserPointsAsyncTask extends AsyncFunnyTask {

    private final User user;

    public DatabaseUpdateUserPointsAsyncTask(User user) {
        this.user = user;
    }

    @Override
    public void execute() {
        DatabaseUserSerializer.updatePoints(this.user);
    }

}

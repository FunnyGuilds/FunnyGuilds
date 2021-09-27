package net.dzikoysk.funnyguilds.concurrency.requests.database;

import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.data.database.DatabaseUser;
import net.dzikoysk.funnyguilds.user.User;

public class DatabaseUpdateUserPointsRequest extends DefaultConcurrencyRequest {

    private final User user;

    public DatabaseUpdateUserPointsRequest(User user) {
        this.user = user;
    }

    @Override
    public void execute() {
        DatabaseUser.updatePoints(user);
    }

}

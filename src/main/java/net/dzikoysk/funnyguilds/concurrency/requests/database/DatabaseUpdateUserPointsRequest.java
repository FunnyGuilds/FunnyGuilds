package net.dzikoysk.funnyguilds.concurrency.requests.database;

import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.data.database.DatabaseUser;

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

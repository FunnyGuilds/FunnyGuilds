package net.dzikoysk.funnyguilds.concurrency.requests.database;

import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.data.database.SQLDataModel;

public class DatabaseUpdateUserPointsRequest extends DefaultConcurrencyRequest {

    private final User user;
    private final SQLDataModel sqlDataModel;

    public DatabaseUpdateUserPointsRequest(SQLDataModel sqlDataModel, User user) {
        this.user = user;
        this.sqlDataModel = sqlDataModel;
    }

    @Override
    public void execute() {
        sqlDataModel.getDatabaseUser().updatePoints(user);
    }

}

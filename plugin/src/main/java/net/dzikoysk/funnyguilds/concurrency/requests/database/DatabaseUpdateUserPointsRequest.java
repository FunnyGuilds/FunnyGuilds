package net.dzikoysk.funnyguilds.concurrency.requests.database;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.data.database.SQLDataModel;
import net.dzikoysk.funnyguilds.data.database.serializer.DatabaseUserSerializer;
import net.dzikoysk.funnyguilds.user.User;

public class DatabaseUpdateUserPointsRequest extends DefaultConcurrencyRequest {

    private final User user;

    public DatabaseUpdateUserPointsRequest(User user) {
        this.user = user;
    }

    @Override
    public void execute() {
        DatabaseUserSerializer.updatePoints((SQLDataModel) FunnyGuilds.getInstance().getDataModel(), this.user);
    }

}

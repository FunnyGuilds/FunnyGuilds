package net.dzikoysk.funnyguilds.ndb.query;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import net.dzikoysk.funnyguilds.ndb.Callback;

public class ReadQuery extends DatabaseQuery {

    public ReadQuery(Callback callback, String query) {
        super(callback, query);
    }

    @Override
    public PreparedStatement prepare(PreparedStatement statement) throws SQLException {
        return statement;
    }
}

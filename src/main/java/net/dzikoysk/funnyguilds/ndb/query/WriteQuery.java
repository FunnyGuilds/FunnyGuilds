package net.dzikoysk.funnyguilds.ndb.query;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WriteQuery extends DatabaseQuery {

    public WriteQuery(String query) {
        super(query);
    }

    @Override
    public PreparedStatement prepare(PreparedStatement statement) throws SQLException {
        return statement;
    }
}

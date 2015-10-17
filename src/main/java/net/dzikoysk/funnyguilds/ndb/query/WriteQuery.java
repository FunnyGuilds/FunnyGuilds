package net.dzikoysk.funnyguilds.ndb.query;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import net.dzikoysk.funnyguilds.ndb.Callback;

public class WriteQuery extends DatabaseQuery {

    public WriteQuery(String query) {
        this(null, query);
    }

    public WriteQuery(Callback callback, String query) {
        super(callback, query);
    }

    @Override
    public PreparedStatement prepare(PreparedStatement statement) throws SQLException {
        return statement;
    }
}

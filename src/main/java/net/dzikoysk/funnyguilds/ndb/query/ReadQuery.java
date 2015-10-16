package net.dzikoysk.funnyguilds.ndb.query;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import net.dzikoysk.funnyguilds.ndb.Callback;

public class ReadQuery extends DatabaseQuery {

    private final Callback callback;

    public ReadQuery(String query, Callback callback) {
        super(query);

        this.callback = callback;
    }

    @Override
    public PreparedStatement prepare(PreparedStatement statement) throws SQLException {
        return statement;
    }

    public Callback getCallback() {
        return this.callback;
    }
}

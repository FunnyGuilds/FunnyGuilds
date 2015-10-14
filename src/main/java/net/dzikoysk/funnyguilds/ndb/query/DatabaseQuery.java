package net.dzikoysk.funnyguilds.ndb.query;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class DatabaseQuery {

    private String query;

    public DatabaseQuery() {
    }

    public DatabaseQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return this.query;
    }

    public boolean isNull() {
        return this.getQuery() == null;
    }

    public abstract PreparedStatement prepare(PreparedStatement statement) throws SQLException;

    public void setQuery(String query) {
        this.query = query;
    }

}

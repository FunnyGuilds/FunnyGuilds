package net.dzikoysk.funnyguilds.ndb.query;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import net.dzikoysk.funnyguilds.ndb.Callback;
import net.dzikoysk.funnyguilds.ndb.DatabaseThread;

public abstract class DatabaseQuery {

    private Callback callback;
    private String query;

    public DatabaseQuery() {
    }

    public DatabaseQuery(Callback callback) {
        this(callback, null);
    }

    public DatabaseQuery(String query) {
        this(null, query);
    }

    public DatabaseQuery(Callback callback, String query) {
        this.callback = callback;
        this.query = query;
    }

    public void execute() {
        DatabaseThread.registerQuery(this);
    }

    public Callback getCallback() {
        return this.callback;
    }

    public String getQuery() {
        return this.query;
    }

    public boolean hasCallback() {
        return this.callback != null;
    }

    public boolean isNull() {
        return this.getQuery() == null;
    }

    public abstract PreparedStatement prepare(PreparedStatement statement) throws SQLException;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}

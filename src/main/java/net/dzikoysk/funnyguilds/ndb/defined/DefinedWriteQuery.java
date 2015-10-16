package net.dzikoysk.funnyguilds.ndb.defined;

import net.dzikoysk.funnyguilds.ndb.Callback;
import net.dzikoysk.funnyguilds.ndb.query.WriteQuery;

public class DefinedWriteQuery extends WriteQuery {

    public DefinedWriteQuery() {
        this(null);
    }

    public DefinedWriteQuery(Callback callback) {
        super(callback, null);
    }
}

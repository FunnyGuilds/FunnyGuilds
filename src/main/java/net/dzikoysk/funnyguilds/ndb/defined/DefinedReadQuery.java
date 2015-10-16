package net.dzikoysk.funnyguilds.ndb.defined;

import net.dzikoysk.funnyguilds.ndb.Callback;
import net.dzikoysk.funnyguilds.ndb.query.ReadQuery;

public class DefinedReadQuery extends ReadQuery {

    public DefinedReadQuery(Callback callback) {
        super(callback, null);
    }
}

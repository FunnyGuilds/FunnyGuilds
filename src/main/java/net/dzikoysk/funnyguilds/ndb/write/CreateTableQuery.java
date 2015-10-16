package net.dzikoysk.funnyguilds.ndb.write;

import net.dzikoysk.funnyguilds.ndb.Table;
import net.dzikoysk.funnyguilds.ndb.defined.DefinedWriteQuery;

public class CreateTableQuery extends DefinedWriteQuery {

    private final Table table;

    public CreateTableQuery(Table table) {
        this.table = table;
    }

    @Override
    public String getQuery() {
        return "CREATE TABLE IF NOT EXISTS `" + this.table.getName() + "` (" + Table.getSchemaString(this.table) + ");";
    }

    public static void queryAll() {
        for (Table table : Table.values())
            new CreateTableQuery(table).execute();
    }
}
